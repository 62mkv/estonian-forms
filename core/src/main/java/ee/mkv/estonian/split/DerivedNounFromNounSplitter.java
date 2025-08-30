package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import org.springframework.stereotype.Component;

@Component
public class DerivedNounFromNounSplitter extends AbstractDerivedSplitter {

    private static final String[] SUFFIXES = {"alism"};

    public DerivedNounFromNounSplitter(FormRepository formRepository) {
        super(formRepository);
    }

    @Override
    protected boolean isSuitable(Form form) {
        return form.getFormTypeCombination().getEkiRepresentation().equals("SgN");
    }

    @Override
    protected String[] getSupportedSuffixes() {
        return SUFFIXES;
    }

    @Override
    protected boolean isLexemeAccepted(Lexeme lexeme) {
        return lexeme.getPartOfSpeech().isNoun();
    }
}
