package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DerivedAdjectiveFromNounSplitter implements LexemeSplitter {
    private static final Set<String> SUPPORTED_SUFFIXES = Set.of("line", "lane");

    private final FormRepository formRepository;

    private static CompoundWord getCompoundWord(Lexeme lexeme, Form form) {
        var result = new CompoundWord();
        result.setCompoundRule(CompoundRule.DERIVED_FROM_NAME_WITH_SUFFIX);
        result.setLexeme(lexeme);
        var component = new CompoundWordComponent();
        component.setForm(form);
        component.setComponentIndex(0);
        component.setComponentStartsAt(0);
        component.setCompoundWord(result);
        result.setComponents(List.of(component));
        return result;
    }

    private static InternalPartOfSpeech getInternalPartOfSpeech(PartOfSpeech partOfSpeech) {
        return InternalPartOfSpeech.fromEkiCodes(partOfSpeech.getEkiCodes());
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        if (getInternalPartOfSpeech(lexeme.getPartOfSpeech()).isName()) {
            return processName(lexeme);
        }
        return Optional.empty();
    }

    private Optional<CompoundWord> processName(Lexeme lexeme) {
        return endsOnSupportedSuffix(lexeme)
                .flatMap(suffix -> buildCompoundWord(lexeme, suffix));
    }

    private Optional<CompoundWord> buildCompoundWord(Lexeme lexeme, String suffix) {
        String representation = lexeme.getLemma().getRepresentation();
        String base = representation.substring(0, representation.length() - suffix.length());

        return formRepository.findWhereRepresentationIn(Set.of(base))
                .stream()
                .filter(form -> isNoun(form.getLexeme().getPartOfSpeech()))
                .filter(SplitUtils::canBeNonLastComponentOfName)
                .findFirst()
                .map(form -> getCompoundWord(lexeme, form));
    }

    private boolean isNoun(PartOfSpeech partOfSpeech) {
        return getInternalPartOfSpeech(partOfSpeech) == InternalPartOfSpeech.NOUN;
    }

    private Optional<String> endsOnSupportedSuffix(Lexeme lexeme) {
        return SUPPORTED_SUFFIXES.stream()
                .filter(suffix -> lexeme.getLemma().getRepresentation().endsWith(suffix))
                .findFirst();
    }

}
