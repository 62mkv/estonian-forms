package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DerivedNameFromVerbSplitter implements LexemeSplitter {

    public static final String SUFFIX = "mine";
    private final FormRepository formRepository;

    private static CompoundWord getCompoundWord(Lexeme lexeme, Form form) {
        var result = new CompoundWord();
        result.setCompoundRule(CompoundRule.DERIVED_FROM_VERB_ROOT_WITH_SUFFIX);
        result.setLexeme(lexeme);
        var component = new CompoundWordComponent();
        component.setForm(form);
        component.setComponentIndex(0);
        component.setComponentStartsAt(0);
        component.setCompoundWord(result);
        result.setComponents(List.of(component));
        return result;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        if (isName(lexeme) && endsOnSupportedSuffix(lexeme)) {
            return buildCompoundWord(lexeme, SUFFIX);
        }
        return Optional.empty();
    }

    private Optional<CompoundWord> buildCompoundWord(Lexeme lexeme, String suffix) {
        String representation = lexeme.getLemma().getRepresentation();
        String base = representation.substring(0, representation.length() - suffix.length());

        return formRepository.findWhereRepresentationIn(Set.of(base))
                .stream()
                .filter(form -> form.getFormTypeCombination().getEkiRepresentation().equals(Constants.VERB_SUPINE_ROOT))
                .findFirst()
                .map(form -> getCompoundWord(lexeme, form));
    }

    private boolean endsOnSupportedSuffix(Lexeme lexeme) {
        return lexeme.getLemma().getRepresentation().endsWith(SUFFIX);
    }

    private boolean isName(Lexeme lexeme) {
        return Objects.equals(InternalPartOfSpeech.fromEkiCodes(lexeme.getPartOfSpeech().getEkiCodes()), InternalPartOfSpeech.NOUN)
                || Objects.equals(InternalPartOfSpeech.fromEkiCodes(lexeme.getPartOfSpeech().getEkiCodes()), InternalPartOfSpeech.ADJECTIVE);
    }
}
