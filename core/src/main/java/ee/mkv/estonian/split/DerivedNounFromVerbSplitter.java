package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import org.springframework.stereotype.Component;

@Component
public class DerivedNounFromVerbSplitter extends AbstractDerivedSplitter {

    private static final String[] SUPPORTED_SUFFIXES = {"mine"};

    public DerivedNounFromVerbSplitter(FormRepository formRepository) {
        super(formRepository);
    }

    @Override
    protected String[] getSupportedSuffixes() {
        return SUPPORTED_SUFFIXES;
    }

    @Override
    protected boolean isLexemeAccepted(Lexeme lexeme) {
        return lexeme.getPartOfSpeech().isNoun();
    }

    @Override
    protected boolean isSuitable(Form form) {
        return form.getFormTypeCombination().getEkiRepresentation().equals("Sup");
    }

    @Override
    protected String findBase(Lexeme lexeme, String suffix) {
        var representation = lexeme.getLemma().getRepresentation();
        return representation.substring(0, representation.length() - suffix.length()) + "ma";
    }

    @Override
    protected CompoundRule getCompoundRule() {
        return CompoundRule.DERIVED_FROM_VERB_SUPINE;
    }
}
