package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
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
public class DerivedNameFromVerbSplitter implements LexemeSplitter {

    public static final String SUFFIX = "mine";
    private final FormRepository formRepository;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        log.info("Trying to split lexeme {} with {}", lexeme, this.getClass().getSimpleName());
        if (lexeme.isName() && endsOnSupportedSuffix(lexeme)) {
            log.info("isName and ends on my suffix {}", lexeme);
            return buildCompoundWord(lexeme, SUFFIX);
        }
        return Optional.empty();
    }

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

    private Optional<CompoundWord> buildCompoundWord(Lexeme lexeme, String suffix) {
        String representation = lexeme.getLemma().getRepresentation();
        String base = representation.substring(0, representation.length() - suffix.length());
        List<Form> forms = formRepository.findWhereRepresentationIn(Set.of(base));
        log.info("Looking for forms for base {}, found {}", base, forms);
        return forms
                .stream()
                .filter(form -> form.getFormTypeCombination().getEkiRepresentation().equals(Constants.VERB_SUPINE_ROOT))
                .findFirst()
                .map(form -> getCompoundWord(lexeme, form));
    }

    private boolean endsOnSupportedSuffix(Lexeme lexeme) {
        return lexeme.getLemma().getRepresentation().endsWith(SUFFIX);
    }

}
