package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DerivedAdjectiveFromVerbSplitter extends AbstractDerivedSplitter {

    private static final String[] SUPPORTED_SUFFIXES = new String[]{"matu"};
    private static final Map<String, String> SUFFIX_TO_DROP = new HashMap<>();
    private static final String EKI_SUPINE_VERB = "Sup";

    static {
        SUFFIX_TO_DROP.put("matu", "tu");
    }

    public DerivedAdjectiveFromVerbSplitter(FormRepository formRepository) {
        super(formRepository);
    }

    @Override
    protected String findBase(Lexeme lexeme, String suffix) {
        return lexeme.getLemma().getRepresentation().substring(0, lexeme.getLemma().getRepresentation().length() - SUFFIX_TO_DROP.get(suffix).length());
    }

    @Override
    protected String[] getSupportedSuffixes() {
        return SUPPORTED_SUFFIXES;
    }

    @Override
    protected boolean isSuitable(Form form) {
        return form.getFormTypeCombination().getEkiRepresentation().equals(EKI_SUPINE_VERB);
    }

    @Override
    protected boolean isLexemeAccepted(Lexeme lexeme) {
        return lexeme.isAdjective();
    }

    @Override
    protected CompoundRule getCompoundRule() {
        return CompoundRule.DERIVED_FROM_VERB_SUPINE;
    }
}
