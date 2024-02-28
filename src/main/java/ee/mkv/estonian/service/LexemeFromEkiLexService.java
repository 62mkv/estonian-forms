package ee.mkv.estonian.service;

import com.github.jsonldjava.shaded.com.google.common.collect.Streams;
import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.error.WordNotFoundException;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LexemeFromEkiLexService {

    private final EkilexWordRepository ekilexWordRepository;
    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final EkilexLexemeRepository ekilexLexemeRepository;
    private final LexemeToEkilexMappingRepository mappingRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;

    @Transactional
    public List<LexemeToEkiLexMapping> buildLexemesFromEkiLexDetails(String lemma) {
        return Streams.stream(ekilexWordRepository.findAllByBaseFormRepresentation(lemma))
                .flatMap(word -> buildLexemesFromEkiLexWord(word.getId()).stream())
                .collect(Collectors.toList());
    }

    /**
     * This will return set of lexemes, built based on existing EkiLex paradigms and forms
     *
     * @param wordId EkiLex wordId
     * @return set of lexemes, built based on existing EkiLex paradigms and forms
     */
    @Transactional
    public List<LexemeToEkiLexMapping> buildLexemesFromEkiLexWord(Long wordId) {
        List<LexemeToEkiLexMapping> result = new ArrayList<>();

        EkilexWord word = ekilexWordRepository.findById(wordId).orElseThrow(() -> new WordNotFoundException(wordId));
        Set<PartOfSpeech> distinctPosForWord = getPartsOfSpeechForEkilexWordId(wordId);

        List<EkilexParadigm> paradigmsForWord = getParadigmsForWordId(wordId);

        for (PartOfSpeech partOfSpeech : distinctPosForWord) {
            result.add(lexemeFromParadigms(paradigmsForWord, word, partOfSpeech));
        }

        if (result.isEmpty()) {
            log.warn("Could not build lexeme from EkiLex word {}", wordId);
        }
        return result;
    }

    private List<EkilexParadigm> getParadigmsForWordId(Long wordId) {
        return Streams.stream(ekilexParadigmRepository.findAllByWordId(wordId))
                .collect(Collectors.toList());
    }

    private Set<PartOfSpeech> getPartsOfSpeechForEkilexWordId(Long wordId) {
        final Iterable<EkilexLexeme> allByWordId = ekilexLexemeRepository.findAllByWordId(wordId);
        return Streams.stream(allByWordId)
                .flatMap(lexeme -> lexeme.getPos().stream())
                .collect(Collectors.toSet());
    }

    private LexemeToEkiLexMapping lexemeFromParadigms(List<EkilexParadigm> paradigms, EkilexWord word, PartOfSpeech partOfSpeech) {
        return mappingRepository.findByEkilexWordAndPartOfSpeech(word, partOfSpeech)
                .orElseGet(() -> {
                    Lexeme lexeme = new Lexeme();

                    lexeme.setLemma(word.getBaseForm());
                    lexeme.setPartOfSpeech(partOfSpeech);
                    lexeme.setForms(buildFormsFromParadigms(paradigms, lexeme));

                    return createMappings(lexeme, word, partOfSpeech);
                });
    }

    private LexemeToEkiLexMapping createMappings(Lexeme lexeme, EkilexWord word, PartOfSpeech partOfSpeech) {
        LexemeToEkiLexMapping mapping = new LexemeToEkiLexMapping();
        mapping.setLexeme(lexeme);
        mapping.setEkilexWord(word);
        mapping.setPartOfSpeech(partOfSpeech);
        return mapping;
    }

    private Set<Form> buildFormsFromParadigms(List<EkilexParadigm> paradigms, Lexeme lexeme) {
        Map<MyFormForLexeme, Set<String>> inflectionTypesPerForm = new HashMap<>();
        for (EkilexParadigm paradigm : paradigms) {
            for (EkilexForm ekilexForm : paradigm.getForms()) {
                final MyFormForLexeme formForLexeme = MyFormForLexeme.fromEkilexForm(ekilexForm);
                if (inflectionTypesPerForm.containsKey(formForLexeme)) {
                    inflectionTypesPerForm.get(formForLexeme).add(paradigm.getInflectionType());
                } else {
                    Set<String> newTypes = new HashSet<>();
                    newTypes.add(paradigm.getInflectionType());
                    inflectionTypesPerForm.put(formForLexeme, newTypes);
                }
            }
        }

        Set<Form> result = new HashSet<>();

        if (inflectionTypesPerForm.isEmpty()) {
            if (lexeme.getPartOfSpeech().getEkiCodes().equals("pf")) {
                Form form = new Form();
                form.setLexeme(lexeme);
                form.setRepresentation(lexeme.getLemma());
                form.setFormTypeCombination(formTypeCombinationRepository.findByEkiRepresentation("pf").orElseThrow());
                lexeme.getForms().add(form);

                result.add(form);
            }
            log.warn("No forms found for lexeme {}", lexeme);
        } else {

            for (Map.Entry<MyFormForLexeme, Set<String>> entry : inflectionTypesPerForm.entrySet()) {
                String inflectionTypes = entry.getValue().stream().sorted().collect(Collectors.joining(","));
                MyFormForLexeme formForLexeme = entry.getKey();

                Form form = new Form();
                form.setDeclinationTypes(inflectionTypes);
                form.setLexeme(lexeme);
                form.setRepresentation(formForLexeme.getRepresentation());
                form.setFormTypeCombination(formForLexeme.getFormTypeCombination());
                lexeme.getForms().add(form);

                result.add(form);
            }
        }

        return result;
    }

    @Transactional
    public Lexeme recoverLexemeFormsFromEkilexForms(Lexeme lexeme, EkilexWord ekilexWord) {
        if (!lexeme.getForms().isEmpty()) {
            log.error("Lexeme {} already has forms", lexeme);
            throw new RuntimeException("Lexeme already has forms");
        }

        List<EkilexParadigm> paradigms = IterableUtils.iterableToList(ekilexParadigmRepository.findAllByWordId(ekilexWord.getId()));
        lexeme.setForms(buildFormsFromParadigms(paradigms, lexeme));
        return lexeme;
    }

    @Data
    static class MyFormForLexeme {
        private final Representation representation;
        private final FormTypeCombination formTypeCombination;

        public MyFormForLexeme(Representation representation, FormTypeCombination formTypeCombination) {
            this.representation = representation;
            this.formTypeCombination = formTypeCombination;
        }

        public static MyFormForLexeme fromEkilexForm(EkilexForm ekilexForm) {
            return new MyFormForLexeme(ekilexForm.getRepresentation(), ekilexForm.getFormTypeCombination());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyFormForLexeme that = (MyFormForLexeme) o;
            return Objects.equals(representation.getId(), that.representation.getId()) &&
                    Objects.equals(formTypeCombination.getId(), that.formTypeCombination.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(representation, formTypeCombination);
        }
    }
}
