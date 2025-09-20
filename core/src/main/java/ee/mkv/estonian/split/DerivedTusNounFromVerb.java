package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import org.springframework.stereotype.Component;

@Component
class DerivedTusNounFromVerb extends AbstractDerivedSplitter {

    private static final String[] SUFFIXES = {"tus"};

    public DerivedTusNounFromVerb(FormRepository formRepository) {
        super(formRepository);
    }

    @Override
    protected boolean isLexemeAccepted(Lexeme lexeme) {
        return lexeme.isNoun() && lexeme.getLemma().getRepresentation().endsWith("tus");
    }

    @Override
    protected String[] getSupportedSuffixes() {
        return SUFFIXES;
    }

    @Override
    protected CompoundRule getCompoundRule() {
        return CompoundRule.DERIVED_NOUN_FROM_VERB_TUS;
    }

    @Override
    protected boolean isSuitable(Form form) {
        var representation = form.getRepresentation().getRepresentation();
        return representation.endsWith("tud");
    }

    @Override
    protected String findBase(Lexeme lexeme, String suffix) {
        return super.findBase(lexeme, suffix).concat("tud");
    }
}