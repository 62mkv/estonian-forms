package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.ekilex.dto.*;
import ee.mkv.estonian.ekilex.error.EkilexParadigmExistsException;
import ee.mkv.estonian.ekilex.error.RepresentationNotAllowedException;
import ee.mkv.estonian.error.FormTypeCombinationNotFound;
import ee.mkv.estonian.error.LanguageNotSupportedException;
import ee.mkv.estonian.error.PartOfSpeechNotFoundException;
import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.service.UserInputProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EkiLexRetrievalService {

    public static final Long INITIAL_WORD_ID = 154_451L;
    public static final String PREFIX_FORM_TYPE_COMBINATION = "ID";
    private final EkiLexClient ekiLexClient;

    private final RepresentationRepository representationsRepository;
    private final EkilexWordRepository ekilexWordRepository;
    private final EkilexLexemeRepository ekilexLexemeRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final EkilexFormRepository ekilexFormRepository;
    private final FormTypeCombinationRepository formTypeRepository;
    private final UserInputProvider userInputProvider;

    public long getLastPersistedWordId() {
        return ekilexWordRepository.getLastRetrievedWordId().orElse(INITIAL_WORD_ID);
    }

    @Transactional
    public List<EkilexWord> retrieveByLemma(String lemma, boolean existingWord) {
        List<EkilexWord> result = new ArrayList<>();
        List<WordDto> words = ekiLexClient.findWords(lemma);
        log.info("Found [{}] for lemma '{}'", words, lemma);
        for (WordDto word : words) {
            try {
                result.add(processWord(word, existingWord));
            } catch (EkilexParadigmExistsException e) {
                log.warn("Paradigm for word {}:{} already exists, skipping", word.getWordId(), word.getWordValue());
            }
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

    @Transactional
    public EkilexWord processWord(WordDto word, boolean force) {
        final Long wordId = word.getWordId();
        boolean existing = ekilexWordRepository.existsById(wordId);
        if (existing && !force) {
            log.warn("EkilexWord with id {} already exists", wordId);
            return ekilexWordRepository.findById(wordId).get();
        }

        if (word.isPrefixoid() && word.getWordTypeCodes().contains("pf")) {
            log.info("Word {} is a prefixoid", wordId);
            return buildPrefixoid(word, existing);
        }

        log.info("Word not found, retrieving from Ekilex: {}", wordId);
        return retrieveFromEkilex(wordId, existing);
    }

    private EkilexWord buildPrefixoid(WordDto word, boolean existing) {
        var ekilexWord = existing
                ? updateEkilexWord(word.getWordId())
                : insertEkilexWord(word);

        var ekilexLexeme = new EkilexLexeme();
        ekilexLexeme.setWord(ekilexWord);
        ekilexLexeme.setPos(Set.of(
                partOfSpeechRepository.findByPartOfSpeechName(EkiPartOfSpeech.PREFIX.getRepresentation())
                        .orElseThrow(() -> new PartOfSpeechNotFoundException(EkiPartOfSpeech.PREFIX.getEkiCodes()))
        ));
        var ekilexParadigm = new EkilexParadigm();
        ekilexParadigm.setWord(ekilexWord);
        var ekilexForm = new EkilexForm();
        ekilexForm.setRepresentation(getRepresentation(word.getWordValue()).get()); //here we should not get empty representation
        ekilexForm.setFormTypeCombination(
                formTypeRepository.findByEkiRepresentation(PREFIX_FORM_TYPE_COMBINATION)
                        .orElseThrow(() -> new FormTypeCombinationNotFound(PREFIX_FORM_TYPE_COMBINATION))
        );
        ekilexWordRepository.save(ekilexWord);
        ekilexLexemeRepository.save(ekilexLexeme);
        ekilexParadigmRepository.save(ekilexParadigm);
        ekilexForm.setEkilexParadigm(ekilexParadigm);
        ekilexFormRepository.save(ekilexForm);
        return ekilexWord;
    }

    private EkilexWord retrieveFromEkilex(Long wordId, boolean existing) {
        DetailsDto detailsDto = ekiLexClient.getDetails(wordId);
        log.debug("Details for word {}: {}", wordId, detailsDto);
        EkilexWord word = existing
                ? updateEkilexWord(wordId)
                : insertEkilexWord(detailsDto.getWord());

        if (existing && ekilexParadigmRepository.existsByWordId(wordId)) {
            // we must make sure there's no pre-existing records, to avoid overwriting those
            throw new EkilexParadigmExistsException(wordId);
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
            myLexemes.add(generateLexemeWithUserChoice(word));
        }

        final List<DetailsParadigmDto> paradigms = detailsDto.getWord().getParadigms();
        if (paradigms != null) {
            for (DetailsParadigmDto paradigmDto : paradigms) {
                saveEkiLexParadigm(word, paradigmDto);
            }
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
        if (wordDto.getWordId() == null) {
            throw new IllegalArgumentException("Word id must be defined");
        }
        var representation = getRepresentation(wordDto.getWordValue())
                .orElseThrow(() -> new RepresentationNotAllowedException(wordDto.getWordValue()));
        EkilexWord word = new EkilexWord();
        word.setId(wordDto.getWordId());
        word.setBaseForm(representation);
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
        getRepresentation(formDto.getValue()).ifPresent(
                representation -> {
                    EkilexForm form = new EkilexForm();
                    form.setEkilexParadigm(paradigm);
                    form.setRepresentation(representation);
                    form.setFormTypeCombination(getFormTypeCombination(formDto.getMorphCode()));
                    ekilexFormRepository.save(form);
                    paradigm.getForms().add(form);
                });
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
            var pos = partOfSpeechRepository.findByPartOfSpeechName(chosenPos.getRepresentation())
                    .orElseThrow(() -> new PartOfSpeechNotFoundException(chosenPos.getRepresentation()));
            partOfSpeeches.add(pos);
        }
        lexeme.setPos(partOfSpeeches);
        return ekilexLexemeRepository.save(lexeme);
    }

    private EkilexLexeme generateLexemeWithUserChoice(EkilexWord word) {
        EkilexLexeme lexeme = new EkilexLexeme();
        lexeme.setWord(word);
        var partOfSpeeches = new HashSet<PartOfSpeech>();
        log.info("Please choose one of the following parts of speech:");
        var chosenPos = showMenu();
        var pos = partOfSpeechRepository.findByPartOfSpeechName(chosenPos.getRepresentation())
                .orElseThrow(() -> new PartOfSpeechNotFoundException(chosenPos.getRepresentation()));
        partOfSpeeches.add(pos);
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

                PartOfSpeech pos = partOfSpeechRepository.findByPartOfSpeechName(posEnum.getRepresentation())
                        .orElseThrow(() -> new PartOfSpeechNotFoundException(posEnum.getRepresentation()));

                result.add(pos);
            }
        }
        return result;
    }

    private Optional<Representation> getRepresentation(String word) {
        if (word == null || word.isBlank()) {
            return Optional.empty();
        }
        var word1 = word.trim();
        if (word1.isEmpty()) {
            log.warn("Empty representation requested, returning empty Optional");
            return Optional.empty();
        }
        if (word1.endsWith("-")) {
            log.warn("Representation ends with hyphen, not allowed: {}", word);
            return Optional.empty();
        }
        return Optional.of(representationsRepository.findByRepresentation(word1)
                .orElseGet(() -> {
                    Representation representation = new Representation();
                    representation.setRepresentation(word1);
                    return representationsRepository.save(representation);
                }));
    }

    private EkiPartOfSpeech showMenu() {
        for (EkiPartOfSpeech option : EkiPartOfSpeech.values()) {
            log.info("{}: {}", option.ordinal() + 1, option.name());
        }

        int input = userInputProvider.getUserChoice(
                Arrays.stream(EkiPartOfSpeech.values())
                        .map(Enum::name)
                        .toArray(String[]::new)
        );

        return EkiPartOfSpeech.from(input);
    }


}
