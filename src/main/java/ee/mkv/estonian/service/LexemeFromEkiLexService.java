package ee.mkv.estonian.service;

import com.github.jsonldjava.shaded.com.google.common.collect.Streams;
import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.error.NonSingularValueException;
import ee.mkv.estonian.model.PartOfSpeechEnum;
import ee.mkv.estonian.repository.EkilexFormRepository;
import ee.mkv.estonian.repository.EkilexParadigmRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LexemeFromEkiLexService {

    private final EkilexParadigmRepository paradigmRepository;
    private final EkilexFormRepository formRepository;

    public LexemeFromEkiLexService(EkilexParadigmRepository paradigmRepository, EkilexFormRepository formRepository) {
        this.paradigmRepository = paradigmRepository;
        this.formRepository = formRepository;
    }

    /**
     * This will returned set of lexemes, built based on existing EkiLex paradigms and forms
     *
     * @param lemma
     * @param partOfSpeech
     * @return
     */
    public Set<Lexeme> buildLexemesFromEkiLexParadigms(String lemma, PartOfSpeechEnum partOfSpeech) {
        Set<Lexeme> result = new HashSet<>();

        Map<Long, Set<EkilexParadigm>> paradigmsByWordId =
                Streams.stream(paradigmRepository.findByBaseFormRepresentationAndPartOfSpeechPartOfSpeech(lemma, partOfSpeech.getRepresentation()))
                        .collect(Collectors.toMap(
                                EkilexParadigm::getWordId,
                                Collections::singleton,
                                (set1, set2) -> {
                                    set1.addAll(set2);
                                    return set1;
                                }
                        ));

        for (Map.Entry<Long, Set<EkilexParadigm>> word : paradigmsByWordId.entrySet()) {
            log.info("Found paradigm: {}", word.getKey());
            result.add(lexemeFromParadigms(word.getValue()));
        }

        return result;
    }

    private Lexeme lexemeFromParadigms(Set<EkilexParadigm> paradigms) {
        Lexeme lexeme = new Lexeme();

        Representation representation = getExactlyOne(
                paradigms.stream().map(EkilexParadigm::getBaseForm).collect(Collectors.toSet()),
                "paradigm",
                "baseForm"
        );
        PartOfSpeech partOfSpeech = getExactlyOne(
                paradigms.stream().map(EkilexParadigm::getPartOfSpeech).collect(Collectors.toSet()),
                "paradigm",
                "partOfSpeech"
        );

        lexeme.setLemma(representation);
        lexeme.setPartOfSpeech(partOfSpeech);
        lexeme.setForms(buildFormsFromParadigms(paradigms, lexeme));
        return lexeme;
    }

    private Set<Form> buildFormsFromParadigms(Set<EkilexParadigm> paradigms, Lexeme lexeme) {
        Map<MyFormForLexeme, Set<String>> inflectionTypesPerForm = new HashMap<>();
        for (EkilexParadigm paradigm : paradigms) {
            for (EkilexForm ekilexForm : paradigm.getForms()) {
                final MyFormForLexeme formForLexeme = MyFormForLexeme.fromEkilexForm(ekilexForm);
                if (inflectionTypesPerForm.containsKey(formForLexeme)) {
                    inflectionTypesPerForm.get(formForLexeme).add(paradigm.getInflectionTypeNr());
                } else {
                    Set<String> newTypes = new HashSet<>();
                    newTypes.add(paradigm.getInflectionTypeNr());
                    inflectionTypesPerForm.put(formForLexeme, newTypes);
                }
            }
        }

        Set<Form> result = new HashSet<>();
        for (Map.Entry<MyFormForLexeme, Set<String>> entry : inflectionTypesPerForm.entrySet()) {
            String inflectionTypes = String.join(",", entry.getValue());
            MyFormForLexeme formForLexeme = entry.getKey();

            Form form = new Form();
            form.setDeclinationTypes(inflectionTypes);
            form.setLexeme(lexeme);
            form.setRepresentation(formForLexeme.getRepresentation());
            form.setFormTypeCombination(formForLexeme.getFormTypeCombination());
            lexeme.getForms().add(form);

            result.add(form);
        }

        return result;
    }

    private <T> T getExactlyOne(Collection<T> items, String objectType, String property) {
        T currentValue = null;
        for (T item : items) {
            if (currentValue == null) {
                currentValue = item;
            } else {
                if (!currentValue.equals(item)) {
                    throw new NonSingularValueException(objectType, property, item);
                }
            }
        }
        return currentValue;
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
