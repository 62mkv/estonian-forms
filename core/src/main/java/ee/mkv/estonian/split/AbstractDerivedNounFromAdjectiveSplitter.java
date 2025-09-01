package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDerivedNounFromAdjectiveSplitter extends AbstractDerivedSplitter {

    protected AbstractDerivedNounFromAdjectiveSplitter(FormRepository formRepository) {
        super(formRepository);
    }

    @Override
    protected CompoundRule getCompoundRule() {
        return CompoundRule.DERIVED_NAME_FROM_ADJECTIVE;
    }

    @Override
    protected boolean isSuitable(Form form) {
        return formTypeMatches(form)
                && form.getLexeme().getPartOfSpeech().getEkiCodes().contains(InternalPartOfSpeech.ADJECTIVE.getEkiCodes());
    }

    @Override
    protected boolean isLexemeAccepted(Lexeme lexeme) {
        return lexeme.getPartOfSpeech().isNoun();
    }

    @Override
    protected String[] getSupportedSuffixes() {
        return new String[]{getSuffix()};
    }

    @Override
    protected String findBase(Lexeme lexeme, String suffix) {
        return getBase(lexeme.getLemma().getRepresentation());
    }

    protected abstract String getSuffix();

    protected abstract boolean formTypeMatches(Form form);

    protected abstract String getBase(String representation);
}