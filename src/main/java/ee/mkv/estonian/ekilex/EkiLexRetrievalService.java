package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.ekilex.dto.*;
import ee.mkv.estonian.error.FormTypeCombinationNotFound;
import ee.mkv.estonian.error.LanguageNotSupportedException;
import ee.mkv.estonian.error.PartOfSpeechNotFoundException;
import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EkiLexRetrievalService {

    public static final Long INITIAL_WORD_ID = 154_451L;
    private final EkiLexClient ekiLexClient;

    private final RepresentationRepository representationsRepository;
    private final EkilexWordRepository ekilexWordRepository;
    private final EkilexLexemeRepository ekilexLexemeRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final EkilexFormRepository ekilexFormRepository;
    private final FormTypeCombinationRepository formTypeRepository;

    public long getLastPersistedWordId() {
        return ekilexWordRepository.getLastRetrievedWordId().orElse(INITIAL_WORD_ID);
    }

    @Transactional
    public List<EkilexWord> retrieveByLemma(String lemma, boolean existingWord) {
        List<EkilexWord> result = new ArrayList<>();
        for (Long wordId : ekiLexClient.findWords(lemma)) {
            result.add(retrieveById(wordId, existingWord));
        }

        return result;
    }

    @Transactional
    public EkilexWord retrieveById(Long wordId, boolean force) {

        if (ekilexWordRepository.existsById(wordId)) {
            if (!force) {
                log.warn("EkilexWord with id {} already exists", wordId);
                return ekilexWordRepository.findById(wordId).get();
            } else {
                log.info("Force retrieval for word {}", wordId);
                return retrieveFromEkilex(wordId, true);
            }
        }

        log.info("Word not found, retrieving from Ekilex: {}", wordId);
        return retrieveFromEkilex(wordId, false);
    }

    private EkilexWord retrieveFromEkilex(Long wordId, boolean existing) {
        DetailsDto detailsDto = ekiLexClient.getDetails(wordId);
        log.debug("Details for word {}: {}", wordId, detailsDto);
        EkilexWord word = existing
                ? updateEkilexWord(wordId)
                : insertEkilexWord(detailsDto.getWord());

        if (existing && ekilexParadigmRepository.existsByWordId(wordId)) {
            // we must make sure there's no pre-existing records, to avoid overwriting those
            throw new RuntimeException("Paradigm exists for word " + word);
        }

        List<EkilexLexeme> myLexemes = new ArrayList<>();
        for (DetailsLexemeDto lexemeDto : detailsDto.getLexemes()) {
            try {
                myLexemes.add(saveEkiLexLexeme(word, lexemeDto));
            } catch (Exception e) {
                log.error("Error while saving a lexeme: {}", e.getMessage());
            }
        }

        if (myLexemes.isEmpty()) {
            log.warn("No lexemes found for word {}", wordId);
            throw new RuntimeException("Won't save EkilexWord without lexemes");
        }

        List<EkilexParadigm> myParadigms = new ArrayList<>();
        for (DetailsParadigmDto paradigmDto : detailsDto.getParadigms()) {
            myParadigms.add(saveEkiLexParadigm(word, paradigmDto));
        }

        return word;
    }

    private EkilexWord updateEkilexWord(Long wordId) {
        return ekilexWordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("This should not happen here"));
    }

    private EkilexWord insertEkilexWord(WordDto wordDto) {
        if (!wordDto.getLang().equalsIgnoreCase("est")) {
            throw new LanguageNotSupportedException(wordDto.getLang());
        }
        EkilexWord word = new EkilexWord();
        word.setId(wordDto.getWordId());
        word.setBaseForm(getRepresentation(wordDto.getWordValue()));
        return ekilexWordRepository.save(word);
    }

    private EkilexParadigm saveEkiLexParadigm(EkilexWord word, DetailsParadigmDto paradigmDto) {
        EkilexParadigm paradigm = new EkilexParadigm();
        paradigm.setWord(word);
        paradigm.setInflectionType(paradigmDto.getInflectionTypeNr());
        final EkilexParadigm ekilexParadigm = ekilexParadigmRepository.save(paradigm);
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
        var partOfSpeeches = getPos(lexemeDto.getPos());
        if (partOfSpeeches.isEmpty()) {
            log.info("Please choose one of the following parts of speech:");
            var chosenPos = showMenu();
            var pos = partOfSpeechRepository.findByPartOfSpeech(chosenPos.getRepresentation())
                    .orElseThrow(() -> new PartOfSpeechNotFoundException(chosenPos.getRepresentation()));
            partOfSpeeches.add(pos);
        }
        lexeme.setPos(partOfSpeeches);
        return ekilexLexemeRepository.save(lexeme);
    }

    private Set<PartOfSpeech> getPos(List<DetailsClassifierDto> posList) {
        Objects.requireNonNull(posList, "pos field must be defined");
        Set<PartOfSpeech> result = new HashSet<>();
        for (DetailsClassifierDto classifierDto : posList) {
            if (classifierDto.getName().equalsIgnoreCase("POS")) {

                EkiPartOfSpeech posEnum = EkiPartOfSpeech.fromEkilexCode(classifierDto.getCode())
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

    private EkiPartOfSpeech showMenu() {
        for (EkiPartOfSpeech option : EkiPartOfSpeech.values()) {
            log.info("{}: {}", option.ordinal() + 1, option.name());
        }
        Scanner in = new Scanner(System.in);
        int input = -1;
        boolean validInput = false;
        do {
            try {
                input = in.nextInt();
                validInput = true;
            } catch (NoSuchElementException e) {
                log.error("Invalid input", e);
            }
        } while (!validInput);
        return EkiPartOfSpeech.from(input);
    }


}
