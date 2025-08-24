package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ee.mkv.estonian.split.SplitUtils.getCompoundWord;

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
        return buildCompoundWord(lexeme, SUFFIX);
    }

    @Override
    public boolean canProcess(Lexeme lexeme) {
        return lexeme.isName() && endsOnSupportedSuffix(lexeme);
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
                .map(form -> getCompoundWord(lexeme, form, CompoundRule.DERIVED_FROM_VERB_ROOT_WITH_SUFFIX));
    }

    private boolean endsOnSupportedSuffix(Lexeme lexeme) {
        return lexeme.getLemma().getRepresentation().endsWith(SUFFIX);
    }

}
