package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.CompoundWord;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DerivedAdjectiveFromVerbSplitter implements LexemeSplitter {

    private static final String[] SUPPORTED_SUFFIXES = new String[]{"matu"};
    private static final Map<String, String> SUFFIX_TO_DROP = new HashMap<>();
    private static final String EKI_SUPINE_VERB = "Sup";

    private final FormRepository formRepository;

    static {
        SUFFIX_TO_DROP.put("matu", "tu");
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        log.info("Trying to split lexeme {} with {}", lexeme, this.getClass().getSimpleName());
        for (String suffix : SUPPORTED_SUFFIXES) {
            if (lexeme.getLemma().getRepresentation().endsWith(suffix)) {
                String toDrop = SUFFIX_TO_DROP.get(suffix);
                String base = lexeme.getLemma().getRepresentation().substring(0, lexeme.getLemma().getRepresentation().length() - toDrop.length());
                var forms = formRepository.findWhereRepresentationIn(java.util.Set.of(base));
                log.info("Looking for forms for base {}, found [{}", base, forms);
                return forms
                        .stream()
                        .peek(form -> log.info("Considering form {} for lexeme {}", form, lexeme))
                        .filter(form -> form.getFormTypeCombination().getEkiRepresentation().equals(EKI_SUPINE_VERB))
                        .peek(form -> log.info("Form {} is verb supine root", form))
                        .findFirst()
                        .map(form -> SplitUtils.getCompoundWord(lexeme, form, CompoundRule.DERIVED_FROM_VERB_SUPINE));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean canProcess(Lexeme lexeme) {
        boolean endsOnSupportedSuffix = false;
        for (String suffix : SUPPORTED_SUFFIXES) {
            if (lexeme.getLemma().getRepresentation().endsWith(suffix)) {
                endsOnSupportedSuffix = true;
                break;
            }
        }
        return endsOnSupportedSuffix;
    }
}
