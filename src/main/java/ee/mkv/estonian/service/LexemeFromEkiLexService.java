package ee.mkv.estonian.service;

import com.github.jsonldjava.shaded.com.google.common.collect.Streams;
import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.error.WordNotFoundException;
import ee.mkv.estonian.repository.EkilexLexemeRepository;
import ee.mkv.estonian.repository.EkilexParadigmRepository;
import ee.mkv.estonian.repository.EkilexWordRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LexemeFromEkiLexService {

    private final EkilexParadigmRepository paradigmRepository;
    private final EkilexLexemeRepository ekilexLexemeRepository;
    private final EkilexWordRepository wordRepository;

    public LexemeFromEkiLexService(EkilexParadigmRepository paradigmRepository, EkilexLexemeRepository ekilexLexemeRepository, EkilexWordRepository wordRepository) {
        this.paradigmRepository = paradigmRepository;
        this.ekilexLexemeRepository = ekilexLexemeRepository;
        this.wordRepository = wordRepository;
    }

    public List<Lexeme> buildLexemesFromEkiLexDetails(String lemma) {
        return Streams.stream(wordRepository.findAllByBaseFormRepresentation(lemma))
                .flatMap(word -> buildLexemesFromEkiLexWord(word.getId()).stream())
                .collect(Collectors.toList());
    }

    /**
     * This will return set of lexemes, built based on existing EkiLex paradigms and forms
     *
     * @param wordId EkiLex wordId
     * @return set of lexemes, built based on existing EkiLex paradigms and forms
     */
    private List<Lexeme> buildLexemesFromEkiLexWord(Long wordId) {
        List<Lexeme> result = new ArrayList<>();

        EkilexWord word = wordRepository.findById(wordId).orElseThrow(() -> new WordNotFoundException(wordId));
        Set<PartOfSpeech> distinctPosForWord = getPartsOfSpeechForEkilexWordId(wordId);

        List<EkilexParadigm> paradigmsForWord = getParadigmsForWordId(wordId);

        for (PartOfSpeech partOfSpeech : distinctPosForWord) {
            result.add(lexemeFromParadigms(paradigmsForWord, word.getBaseForm(), partOfSpeech));
        }

        return result;
    }

    private List<EkilexParadigm> getParadigmsForWordId(Long wordId) {
        return Streams.stream(paradigmRepository.findAllByWordId(wordId))
                .collect(Collectors.toList());
    }

    private Set<PartOfSpeech> getPartsOfSpeechForEkilexWordId(Long wordId) {
        final Iterable<EkilexLexeme> allByWordId = ekilexLexemeRepository.findAllByWordId(wordId);
        return Streams.stream(allByWordId)
                .flatMap(lexeme -> lexeme.getPos().stream())
                .collect(Collectors.toSet());
    }

    private Lexeme lexemeFromParadigms(List<EkilexParadigm> paradigms, Representation representation, PartOfSpeech partOfSpeech) {
        Lexeme lexeme = new Lexeme();

        lexeme.setLemma(representation);
        lexeme.setPartOfSpeech(partOfSpeech);
        lexeme.setForms(buildFormsFromParadigms(paradigms, lexeme));
        return lexeme;
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

        return result;
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
