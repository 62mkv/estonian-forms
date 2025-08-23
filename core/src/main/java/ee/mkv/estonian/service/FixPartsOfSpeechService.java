package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.error.ProcessingException;
import ee.mkv.estonian.model.HollowForm;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.*;

@Component
@Slf4j
public class FixPartsOfSpeechService {
    private final static Set<String> BASIC_FORM_CODES = initializeBasicFormCodes();
    private final EkilexWordRepository ekilexWordRepository;
    private final LexemeRepository lexemeRepository;
    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final EkilexLexemeRepository ekilexLexemeRepository;
    private final LexemeToEkilexMappingRepository lexemeToEkilexMappingRepository;
    private final PlatformTransactionManager transactionManager;

    public FixPartsOfSpeechService(EkilexWordRepository ekilexWordRepository, LexemeRepository lexemeRepository, EkilexParadigmRepository ekilexParadigmRepository, EkilexLexemeRepository ekilexLexemeRepository, LexemeToEkilexMappingRepository lexemeToEkilexMappingRepository, PlatformTransactionManager transactionManager) {
        this.ekilexWordRepository = ekilexWordRepository;
        this.lexemeRepository = lexemeRepository;
        this.ekilexParadigmRepository = ekilexParadigmRepository;
        this.ekilexLexemeRepository = ekilexLexemeRepository;
        this.lexemeToEkilexMappingRepository = lexemeToEkilexMappingRepository;
        this.transactionManager = transactionManager;
    }

    private static Set<String> initializeBasicFormCodes() {
        return new HashSet<>(Arrays.asList("SgN", "SgG", "SgP", "SgIll", "SgIn", "PlN", "PlG", "PlP", "PlIll", "PlIn", "Sup", "Inf", "IndPrSg1"));
    }

    public void fixPartsOfSpeech() {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        for (EkilexWord ekilexWord : ekilexWordRepository.findAll()) {
            if (ekilexParadigmRepository.existsByWordId(ekilexWord.getId())
                    && !lexemeToEkilexMappingRepository.existsByEkilexWordId(ekilexWord.getId())
                    && !ekilexLexemeRepository.existsByWordId(ekilexWord.getId())
            ) {
                try {
                    if (status.isCompleted()) {
                        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
                    }
                    processWord(ekilexWord);
                    transactionManager.commit(status);
                } catch (ProcessingException e) {
                    // just keep going
                } catch (Exception e) {
                    log.error("Exception {}:{}", e.getClass().getSimpleName(), e.getMessage());
                    status.setRollbackOnly();
                    break;
                }
            }
        }
    }

    private void processWord(EkilexWord word) throws ProcessingException {
        log.info("Processing Ekilex word: {} {}", word.getId(), word.getBaseForm().getRepresentation());
        // check if there is a lexeme with same base form
        boolean finished = false;
        final List<Lexeme> lexemesWithSameLemma = lexemeRepository.findByLemma(word.getBaseForm());
        if (!lexemesWithSameLemma.isEmpty()) {
            log.warn("There're some lexemes with same lemma: {} of them", lexemesWithSameLemma.size());
            // if there is - check all the forms, if they match
            for (Lexeme lexeme : lexemesWithSameLemma) {
                if (matchesByForms(word, lexeme, "")) {
                    // if they match - assign a lexeme to this ekilexWord and proceed with next
                    assignLexemeToWord(word, lexeme);
                    finished = true;
                    break;
                }
            }
        }

        if (!finished) {
            processWordWithoutMatchingLexemes(word);
        }
    }

    private void processWordWithoutMatchingLexemes(EkilexWord word) throws ProcessingException {
        final String baseForm = word.getBaseForm().getRepresentation();
        if (baseForm.contains(" ")) {
            throw new ProcessingException(String.format("Word '%s' contains a space; will be skipped", baseForm));
        }

        Set<String> tails = StringUtils.getTails(baseForm, 3);
        if (tails.isEmpty())
            return;

        // find all the tails of a "word" that is a base form of other lexeme
        List<Lexeme> matchingByTail = new ArrayList<>();
        lexemeRepository.findByLemmaRepresentationIn(tails)
                .forEach(lexeme -> {
                    log.info("Found a lexeme for tail {} with part of speech {}", lexeme.getLemma().getRepresentation(), lexeme.getPartOfSpeech().getPartOfSpeechName());
                    matchingByTail.add(lexeme);
                });

        Optional<Lexeme> bestMatching = matchingByTail
                .stream()
                .sorted(Comparator.comparing(lexeme -> lexeme.getLemma().getRepresentation().length()))
                .filter(lexeme -> matchesByForms(word, lexeme, StringUtils.getHeadForTail(baseForm, lexeme.getLemma().getRepresentation())))
                .findFirst();

        bestMatching.ifPresent(lexeme -> {
            log.info("Found matching lexeme with forms {} for word {}", lexeme.getLemma().getRepresentation(), baseForm);
            EkilexLexeme patchedLexeme = new EkilexLexeme();
            patchedLexeme.setWord(word);
            patchedLexeme.setPos(Collections.singleton(lexeme.getPartOfSpeech()));
            EkilexLexeme savedLexeme = ekilexLexemeRepository.save(patchedLexeme);
            log.info("Saved new EkilexLexeme for word {} with part of speech {} and id {}",
                    word.getId(), lexeme.getPartOfSpeech(), savedLexeme.getId()
            );
        });
        //   start with the longest tail
        //      check if all of the SgGen, SgPt, PlNom, PlGen, PlPt forms of ekilex word
        //        end with corresponding forms of the lexeme that the tail belongs to
        //      if some not match - report a warning and proceed to next longest tail
        //      if all forms match, create an "ekilex_lexeme" that has the same partOfSpeech as the
        //         lexeme of the "tail"
    }

    private void assignLexemeToWord(EkilexWord word, Lexeme lexeme) {
        // actually this does not make any sense - it would just add a duplicated lexeme, so it's a no-op
    }

    private boolean matchesByForms(EkilexWord word, Lexeme lexeme, String prefix) {
        Set<HollowForm> ekilexForms = buildFormModelForEkilexWord(word);
        Set<HollowForm> lexemeForms = buildFormModelForLexemeWithPrefix(lexeme, prefix);
        return ekilexForms.containsAll(lexemeForms) && lexemeForms.containsAll(ekilexForms);
    }

    private Set<HollowForm> buildFormModelForLexemeWithPrefix(Lexeme lexeme, String prefix) {
        Set<HollowForm> result = new HashSet<>();

        for (Form form : lexeme.getForms()) {
            final String ekiRepresentation = form.getFormTypeCombination().getEkiRepresentation();
            if (BASIC_FORM_CODES.contains(ekiRepresentation)) {
                result.add(new HollowForm(ekiRepresentation, prefix.concat(form.getRepresentation().getRepresentation())));
            }
        }

        return result;
    }

    private Set<HollowForm> buildFormModelForEkilexWord(EkilexWord word) {
        Set<HollowForm> result = new HashSet<>();

        for (EkilexParadigm paradigm : ekilexParadigmRepository.findAllByWordId(word.getId())) {
            result.addAll(buildFormModelForEkilexParadigm(paradigm));
        }

        return result;
    }

    private Set<HollowForm> buildFormModelForEkilexParadigm(EkilexParadigm paradigm) {
        Set<HollowForm> result = new HashSet<>();

        for (EkilexForm form : paradigm.getForms()) {
            final String ekiRepresentation = form.getFormTypeCombination().getEkiRepresentation();
            if (BASIC_FORM_CODES.contains(ekiRepresentation)) {
                result.add(new HollowForm(ekiRepresentation, form.getRepresentation().getRepresentation()));
            }
        }

        return result;
    }
}
