package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.domain.EkilexParadigm;
import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.ekilex.dto.*;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.service.UserInputProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("h2test")
@DataJpaTest
class EkiLexRetrievalServiceTest {

    private final String[] formTypeCodes = new String[]{"SgN", "SgG", "SgP", "SgIll"};

    @MockBean
    private EkiLexClient ekiLexClient;

    private EkiLexRetrievalService sut;

    @Autowired
    private RepresentationRepository representationsRepository;

    @Autowired
    private EkilexWordRepository wordRepository;

    @Autowired
    private PartOfSpeechRepository partOfSpeechRepository;

    @Autowired
    private EkilexParadigmRepository paradigmRepository;

    @Autowired
    private EkilexFormRepository ekilexFormRepository;

    @Autowired
    private FormTypeCombinationRepository formTypeRepository;

    @MockBean
    UserInputProvider userInputProvider;

    @BeforeEach
    void setUp() {
        sut = new EkiLexRetrievalService(ekiLexClient, representationsRepository, wordRepository, paradigmRepository, ekilexFormRepository, formTypeRepository, userInputProvider);
    }

    @Test
    @Sql("classpath:sql/retrieval-koer.sql")
    void testKoer() {
        final long wordId = 1L;
        var wordDto = getWordDto(wordId, "koer");
        given(this.ekiLexClient.findWords("koer")).willReturn(List.of(wordDto));
        given(this.ekiLexClient.getDetails(wordId)).willReturn(getKoerDetailsDto());
        final List<EkilexWord> ekilexWords = sut.retrieveByLemma("koer", false);
        assertThat(ekilexWords).hasSize(1);

        Iterable<EkilexParadigm> paradigms = paradigmRepository.findAllByWordId(wordId);
        assertThat(paradigms)
                .hasSize(3)
                .allMatch(ekilexParadigm -> ekilexParadigm.getForms().size() == 3);

        EkilexWord ekilexWord = wordRepository.findById(wordId).orElseThrow();
        assertThat(ekilexWord.getPartsOfSpeech())
                .containsExactlyInAnyOrder(InternalPartOfSpeech.ADJECTIVE, InternalPartOfSpeech.NOUN);
    }

    private DetailsDto getKoerDetailsDto() {
        Map<String, String> paradigm1 = getParadigmAsMap("koer", "koera", "koerat");
        Map<String, String> paradigm2 = getParadigmAsMap("koer", "koeru", "koerut");
        Map<String, String> paradigm3 = getParadigmAsMap("koer", "koeri", "koerit");

        return getDetailsDto(1L,
                "koer",
                Arrays.asList("s", "adj"),
                Arrays.asList(paradigm1, paradigm2, paradigm3)
        );
    }

    private Map<String, String> getParadigmAsMap(String... values) {
        Map<String, String> result = new HashMap<>();

        int i = 0;
        for (String value : values) {
            if (i < formTypeCodes.length) {
                result.put(formTypeCodes[i], value);
                i++;
            }
        }

        return result;
    }

    private DetailsDto getDetailsDto(Long wordId,
                                     String lemma,
                                     List<String> partsOfSpeech,
                                     List<Map<String, String>> paradigms) {
        DetailsDto detailsDto = new DetailsDto();
        WordDto wordDto = getWordDto(wordId, lemma);
        wordDto.setParadigms(getParadigmDtoList(paradigms));
        detailsDto.setWord(wordDto);
        detailsDto.setLexemes(getLexemeDtoList(wordId, partsOfSpeech));
        return detailsDto;
    }

    private List<DetailsParadigmDto> getParadigmDtoList(List<Map<String, String>> paradigms) {
        List<DetailsParadigmDto> result = new ArrayList<>();

        int i = 1;
        for (Map<String, String> paradigm : paradigms) {
            DetailsParadigmDto paradigmDto = new DetailsParadigmDto();
            paradigmDto.setParadigmId((long) i);
            paradigmDto.setInflectionTypeNr("22");
            paradigmDto.setForms(getFormDtoList(paradigm));
            result.add(paradigmDto);
        }
        return result;
    }

    private List<FormDto> getFormDtoList(Map<String, String> paradigm) {
        return paradigm.entrySet()
                .stream()
                .map(entry -> {
                    FormDto formDto = new FormDto();
                    formDto.setMorphCode(entry.getKey());
                    formDto.setValue(entry.getValue());
                    return formDto;
                })
                .toList();
    }

    private List<DetailsLexemeDto> getLexemeDtoList(Long wordId, List<String> partsOfSpeech) {
        List<DetailsLexemeDto> result = new ArrayList<>();
        int i = 1;
        for (String code : partsOfSpeech) {
            DetailsLexemeDto lexemeDto = new DetailsLexemeDto();
            lexemeDto.setLexemeId((long) i++);
            lexemeDto.setWordId(wordId);
            lexemeDto.setPos(getClassifierDtos(code));
            result.add(lexemeDto);
        }

        return result;
    }

    private List<DetailsClassifierDto> getClassifierDtos(String code) {
        List<DetailsClassifierDto> posList = new ArrayList<>();
        DetailsClassifierDto classifierDto = new DetailsClassifierDto();
        classifierDto.setName("POS");
        classifierDto.setCode(code);
        posList.add(classifierDto);
        return posList;
    }

    private WordDto getWordDto(Long wordId, String lemma) {
        WordDto wordDto = new WordDto();
        wordDto.setWordId(wordId);
        wordDto.setWordClass("noomen");
        wordDto.setLang("est");
        wordDto.setWordValue(lemma);
        wordDto.setHomonymNr(1);
        return wordDto;
    }
}