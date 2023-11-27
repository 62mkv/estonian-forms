package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.ekilex.dto.*;
import ee.mkv.estonian.error.FormTypeCombinationNotFound;
import ee.mkv.estonian.error.LanguageNotSupportedException;
import ee.mkv.estonian.error.NotImplementedException;
import ee.mkv.estonian.error.PartOfSpeechNotFoundException;
import ee.mkv.estonian.model.PartOfSpeechEnum;
import ee.mkv.estonian.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
public class EkiLexRetrievalService {

    public static final Long INITIAL_WORD_ID = 154_451L;
    private final EkiLexClient ekiLexClient;

    private final RepresentationsRepository representationsRepository;
    private final EkilexWordRepository wordRepository;
    private final EkilexLexemeRepository lexemeRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final EkilexParadigmRepository paradigmRepository;
    private final EkilexFormRepository ekilexFormRepository;
    private final FormTypeCombinationRepository formTypeRepository;

    public EkiLexRetrievalService(EkiLexClient ekiLexClient, RepresentationsRepository representationsRepository, EkilexWordRepository wordRepository, EkilexLexemeRepository lexemeRepository, PartOfSpeechRepository partOfSpeechRepository, EkilexParadigmRepository paradigmRepository, EkilexFormRepository ekilexFormRepository, FormTypeCombinationRepository formTypeRepository) {
        this.ekiLexClient = ekiLexClient;
        this.representationsRepository = representationsRepository;
        this.wordRepository = wordRepository;
        this.lexemeRepository = lexemeRepository;
        this.partOfSpeechRepository = partOfSpeechRepository;
        this.paradigmRepository = paradigmRepository;
        this.ekilexFormRepository = ekilexFormRepository;
        this.formTypeRepository = formTypeRepository;
    }

    public long getLastPersistedWordId() {
        return wordRepository.getLastRetrievedWordId().orElse(INITIAL_WORD_ID);
    }

    @Transactional
    public List<EkilexWord> retrieveByLemma(String lemma, boolean forceOverwrite) {
        List<EkilexWord> result = new ArrayList<>();
        for (Long wordId : ekiLexClient.findWords(lemma)) {
            result.add(retrieveById(wordId, forceOverwrite));
        }

        return result;
    }

    @Transactional
    public EkilexWord retrieveById(Long wordId, boolean forceOverwrite) {

        if (wordRepository.existsById(wordId)) {
            if (!forceOverwrite) {
                log.warn("EkilexWord with id {} already exists", wordId);
                return wordRepository.findById(wordId).get();
            } else {
                throw new NotImplementedException("Force overwrite for existing EkiLex words not implemented yet!");
            }
        }

        DetailsDto detailsDto = ekiLexClient.getDetails(wordId);

        EkilexWord word = getEkilexWord(detailsDto.getWord());

        List<EkilexLexeme> myLexemes = new ArrayList<>();
        for (DetailsLexemeDto lexemeDto : detailsDto.getLexemes()) {
            try {
                myLexemes.add(saveEkiLexLexeme(word, lexemeDto));
            } catch (Exception e) {
                log.error("Error while saving a lexeme: {}", e.getMessage());
            }
        }

        List<EkilexParadigm> myParadigms = new ArrayList<>();
        for (DetailsParadigmDto paradigmDto : detailsDto.getParadigms()) {
            myParadigms.add(saveEkiLexParadigm(word, paradigmDto));
        }

        return word;
    }

    private EkilexWord getEkilexWord(WordDto wordDto) {
        if (!wordDto.getLang().equalsIgnoreCase("est")) {
            throw new LanguageNotSupportedException(wordDto.getLang());
        }
        EkilexWord word = new EkilexWord();
        word.setId(wordDto.getWordId());
        word.setBaseForm(getRepresentation(wordDto.getWordValue()));
        wordRepository.save(word);
        return word;
    }

    private EkilexParadigm saveEkiLexParadigm(EkilexWord word, DetailsParadigmDto paradigmDto) {
        EkilexParadigm paradigm = new EkilexParadigm();
        paradigm.setWord(word);
        paradigm.setInflectionType(paradigmDto.getInflectionTypeNr());
        final EkilexParadigm ekilexParadigm = paradigmRepository.save(paradigm);
        for (FormDto formDto : paradigmDto.getForms()) {
            try {
                saveEkiLexForm(paradigm, formDto);
            } catch (Exception e) {
                log.error("Error while saving form: {}", e.getMessage());
            }
        }

        return ekilexParadigm;
    }

    private void saveEkiLexForm(EkilexParadigm paradigm, FormDto formDto) {
        EkilexForm form = new EkilexForm();
        form.setEkilexParadigm(paradigm);
        form.setRepresentation(getRepresentation(formDto.getValue()));
        form.setFormTypeCombination(getFormTypeCombination(formDto.getMorphCode()));
        ekilexFormRepository.save(form);
        paradigm.getForms().add(form);
    }

    private FormTypeCombination getFormTypeCombination(String morphCode) {
        return formTypeRepository.findByEkiRepresentation(morphCode)
                .orElseThrow(() -> new FormTypeCombinationNotFound(morphCode));
    }

    private EkilexLexeme saveEkiLexLexeme(EkilexWord word, DetailsLexemeDto lexemeDto) {
        EkilexLexeme lexeme = new EkilexLexeme();
        lexeme.setWord(word);
        lexeme.setPos(getPos(lexemeDto.getPos()));
        return lexemeRepository.save(lexeme);
    }

    private Set<PartOfSpeech> getPos(List<DetailsClassifierDto> posList) {
        Objects.requireNonNull(posList, "pos field must be defined");
        Set<PartOfSpeech> result = new HashSet<>();
        for (DetailsClassifierDto classifierDto : posList) {
            if (classifierDto.getName().equalsIgnoreCase("POS")) {

                PartOfSpeechEnum posEnum = PartOfSpeechEnum.fromEkilexCode(classifierDto.getCode())
                        .orElseThrow(() -> new PartOfSpeechNotFoundException(classifierDto.getCode()));

                PartOfSpeech pos = partOfSpeechRepository.findByPartOfSpeech(posEnum.getRepresentation())
                        .orElseThrow(() -> new PartOfSpeechNotFoundException(posEnum.getRepresentation()));

                result.add(pos);
            }
        }
        return result;
    }

    private Representation getRepresentation(String word) {
        return representationsRepository.findByRepresentation(word)
                .orElseGet(() -> {
                    Representation representation = new Representation();
                    representation.setRepresentation(word);
                    return representationsRepository.save(representation);
                });
    }
}
