package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.ekilex.dto.WordDto;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.service.UserInputProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EkiLexRetrievalServiceTest {

    private static final String PREFIX_CODE = "pf";
    private static final String IMMUTABLE_CODE = "ID";

    @InjectMocks
    private final EkiLexRetrievalService sut = null;

    @Mock
    private EkiLexClient ekiLexClient;

    @Mock
    private RepresentationRepository representationsRepository;
    @Mock
    private EkilexWordRepository ekilexWordRepository;
    @Mock
    private EkilexParadigmRepository ekilexParadigmRepository;
    @Mock
    private EkilexFormRepository ekilexFormRepository;
    @Mock
    private FormTypeCombinationRepository formTypeRepository;
    @Mock
    private UserInputProvider userInputProvider;

    @NotNull
    private static FormTypeCombination getFormTypeCombination() {
        FormTypeCombination ftc = new FormTypeCombination();
        ftc.setEkiRepresentation(PREFIX_CODE);
        return ftc;
    }

    @Test
    void testProcessPrefixoid() {
        var wordDto = new WordDto();
        final long wordId = 1428049L;
        wordDto.setWordId(wordId);
        final String wordValue = "lähis";
        wordDto.setWordValue(wordValue);
        wordDto.setPrefixoid(true);
        wordDto.setWordTypeCodes(List.of(PREFIX_CODE));
        wordDto.setLang("est");
        when(ekilexWordRepository.existsById(wordId)).thenReturn(false);
        final Representation representation = getRepresentation(wordValue);
        when(representationsRepository.findByRepresentation(wordValue)).thenReturn(Optional.of(representation));
        final FormTypeCombination ftc = getFormTypeCombination();
        when(formTypeRepository.findByEkiRepresentation(IMMUTABLE_CODE)).thenReturn(Optional.of(ftc));
        EkilexWord expected = new EkilexWord();
        expected.setId(wordId);
        expected.setBaseForm(representation);
        expected.setPartsOfSpeech(Set.of(InternalPartOfSpeech.PREFIX));
        when(ekilexWordRepository.save(expected)).thenReturn(expected);

        var result = sut.processWord(wordDto, false);
        assertEquals(result, expected);
    }

    private Representation getRepresentation(String representation) {
        var rep = new Representation();
        rep.setRepresentation(representation);
        return rep;
    }
}