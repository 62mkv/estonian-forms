package ee.mkv.estonian.split;


import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.CompoundWord;
import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
abstract class AbstractDerivedSplitter implements LexemeSplitter {

    private final FormRepository formRepository;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        log.info("Trying to split lexeme {} with {}", lexeme, this.getClass().getSimpleName());
        for (String suffix : getSupportedSuffixes()) {
            if (lexeme.getLemma().getRepresentation().endsWith(suffix)) {
                String base = findBase(lexeme, suffix);
                var forms = formRepository.findWhereRepresentationIn(Set.of(base));
                log.info("Looking for forms for base {}, found [{}", base, forms);
                return forms
                        .stream()
                        .peek(form -> log.info("Considering form {} for lexeme {}", form, lexeme))
                        .filter(this::isSuitable)
                        .peek(form -> log.info("Form {} is verb supine root", form))
                        .findFirst()
                        .map(form -> SplitUtils.getCompoundWord(lexeme, form, getCompoundRule()));
            }
        }
        return Optional.empty();
    }

    protected abstract CompoundRule getCompoundRule();

    protected abstract boolean isSuitable(Form form);

    @Override
    public boolean canProcess(Lexeme lexeme) {
        boolean endsOnSupportedSuffix = false;
        for (String suffix : getSupportedSuffixes()) {
            if (lexeme.getLemma().getRepresentation().endsWith(suffix)) {
                endsOnSupportedSuffix = true;
                break;
            }
        }
        return endsOnSupportedSuffix && isLexemeAccepted(lexeme);
    }

    protected abstract boolean isLexemeAccepted(Lexeme lexeme);

    protected abstract String[] getSupportedSuffixes();

    protected String findBase(Lexeme lexeme, String suffix) {
        return lexeme.getLemma().getRepresentation().substring(0, lexeme.getLemma().getRepresentation().length() - suffix.length());
    }
}
