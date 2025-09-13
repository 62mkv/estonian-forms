package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DerivedVerbSplitter extends AbstractDerivedSplitter {

    private static final String[] SUFFIXES = {"iseerima"};

    protected DerivedVerbSplitter(FormRepository formRepository) {
        super(formRepository);
    }

    @Override
    protected boolean isLexemeAccepted(Lexeme lexeme) {
        return lexeme.isVerb();
    }

    @Override
    protected CompoundRule getCompoundRule() {
        return CompoundRule.DERIVED_VERB;
    }


    @Override
    protected boolean isSuitable(Form form) {
        // so far, we don't know of any restrictions
        return true;
    }

    @Override
    protected String[] getSupportedSuffixes() {
        return SUFFIXES;
    }

    @Override
    protected Set<String> getCandidates(String base, Lexeme lexeme, String suffix) {
        if (base.endsWith("al") && suffix.equals("iseerima")) {
            // e.g. "professionaliseerima" <- "professionaal" or "professional"
            return Set.of(base, base.substring(0, base.length() - 2) + "aal");
        } else {
            return super.getCandidates(base, lexeme, suffix);
        }
    }
}
