package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import org.springframework.stereotype.Component;

@Component
public class DerivedAdverbFromVerbSplitter extends AbstractDerivedSplitter {

    private static final String[] SUFFIXES = {"tult"};

    public DerivedAdverbFromVerbSplitter(FormRepository formRepository) {
        super(formRepository);
    }

    @Override
    protected String[] getSupportedSuffixes() {
        return SUFFIXES;
    }

    @Override
    protected CompoundRule getCompoundRule() {
        return CompoundRule.DERIVED_ADVERB;
    }

    @Override
    protected boolean isSuitable(Form form) {
        return form.getLexeme().isVerb() &&
                form.getFormTypeCombination().getEkiRepresentation().equals("Sup");
    }

    @Override
    protected boolean isLexemeAccepted(Lexeme lexeme) {
        return lexeme.isAdverb();
    }

    @Override
    protected String findBase(Lexeme lexeme, String suffix) {
        return super.findBase(lexeme, suffix) + "ma";
    }
}
