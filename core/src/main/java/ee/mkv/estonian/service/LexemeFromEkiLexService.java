package ee.mkv.estonian.service;

import com.github.jsonldjava.shaded.com.google.common.collect.Streams;
import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.error.EkilexWordNotFoundException;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LexemeFromEkiLexService {

    private final EkilexWordRepository ekilexWordRepository;
    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final LexemeToEkilexMappingRepository mappingRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private final PartOfSpeechUserInputProvider partOfSpeechUserInputProvider;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final EkilexPartOfSpeechService ekilexPartOfSpeechService;

    @Transactional
    public List<LexemeToEkiLexMapping> buildLexemesFromEkiLexDetails(String lemma) {
        return Streams.stream(ekilexWordRepository.findAllByBaseFormRepresentation(lemma))
                .flatMap(word -> buildLexemesFromEkiLexWord(word.getId()).stream())
                .toList();
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

        EkilexWord word = ekilexWordRepository.findById(wordId).orElseThrow(() -> new EkilexWordNotFoundException(wordId));
        var wordPos = word.getPartsOfSpeech();

        if (wordPos.isEmpty()) {
            log.warn("No parts of speech found for word {}:{}", wordId, word.getBaseForm());
            PartOfSpeech pos = IterableUtils.getFirstValueOrFail(partOfSpeechUserInputProvider.getPartOfSpeech());
            ekilexPartOfSpeechService.assignPosToEkilexWord(wordId, pos);
        }

        List<EkilexParadigm> paradigmsForWord = getParadigmsForWordId(wordId);

        for (var partOfSpeech : wordPos) {
            result.add(lexemeFromParadigms(paradigmsForWord, word, partOfSpeech));
        }

        if (result.isEmpty()) {
            log.warn("Could not build lexeme from EkiLex word {}, possibly no parts of speech were found", wordId);
        }
        return result;
    }

    private List<EkilexParadigm> getParadigmsForWordId(Long wordId) {
        return Streams.stream(ekilexParadigmRepository.findAllByWordId(wordId))
                .toList();
    }

    private LexemeToEkiLexMapping lexemeFromParadigms(List<EkilexParadigm> paradigms, EkilexWord word, InternalPartOfSpeech pos) {
        var partOfSpeech = partOfSpeechRepository.findByEkiCodes(pos.getEkiCodes()).orElseThrow();
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
        Set<Form> forms = Optional.ofNullable(lexeme.getForms()).orElseGet(HashSet::new);
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

        var formAccumulator = new FormAccumulator(forms, lexeme);

        if (inflectionTypesPerForm.isEmpty()) {
            if (InternalPartOfSpeech.fromEkiCodes(lexeme.getPartOfSpeech().getEkiCodes()) == InternalPartOfSpeech.PREFIX) {
                Form form = new Form();
                form.setLexeme(lexeme);
                form.setRepresentation(lexeme.getLemma());
                form.setFormTypeCombination(formTypeCombinationRepository.findByEkiRepresentation(Constants.IMMUTABLE_FORM).orElseThrow());
                lexeme.getForms().add(form);

                formAccumulator.accept(form);
            }
            log.warn("No forms found for lexeme {}", lexeme);
        } else {

            for (var entry : inflectionTypesPerForm.entrySet()) {
                String inflectionTypes = entry.getValue().stream().sorted().collect(Collectors.joining(","));
                MyFormForLexeme formForLexeme = entry.getKey();

                Form form = new Form();
                form.setDeclinationTypes(inflectionTypes);
                form.setLexeme(lexeme);
                form.setRepresentation(formForLexeme.getRepresentation());
                form.setFormTypeCombination(formForLexeme.getFormTypeCombination());
                lexeme.getForms().add(form);

                formAccumulator.accept(form);
            }
        }

        return formAccumulator.getAggregate();
    }

    @Transactional
    public Lexeme recoverLexemeFormsFromEkilexForms(Lexeme lexeme, EkilexWord ekilexWord) {
        if (!lexeme.getForms().isEmpty()) {
            log.warn("Lexeme {} already has [{}] forms", lexeme, lexeme.getForms());
        }

        List<EkilexParadigm> paradigms = IterableUtils.iterableToList(ekilexParadigmRepository.findAllByWordId(ekilexWord.getId()));
        lexeme.setForms(buildFormsFromParadigms(paradigms, lexeme));
        return lexeme;
    }

    private static class FormAccumulator implements Consumer<Form> {
        private final Set<Form> forms;
        private final Lexeme lexeme;

        public FormAccumulator(Set<Form> forms, Lexeme lexeme) {
            this.forms = new HashSet<>(forms);
            this.lexeme = lexeme;
        }

        @Override
        public void accept(Form form) {
            boolean alreadyExists = forms.stream()
                    .anyMatch(existingForm -> existingForm.getRepresentation().equals(form.getRepresentation()) &&
                            existingForm.getFormTypeCombination().equals(form.getFormTypeCombination()));
            if (!alreadyExists) {
                forms.add(form);
            } else {
                log.warn("Form with representation '{}' and type '{}' already exists in the lexeme {}, skipping addition",
                        lexeme, form.getRepresentation(), form.getFormTypeCombination());
            }
        }

        public Set<Form> getAggregate() {
            return this.forms;
        }
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
