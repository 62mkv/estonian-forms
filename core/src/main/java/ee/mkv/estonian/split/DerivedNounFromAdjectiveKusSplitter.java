package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.repository.FormRepository;
import org.springframework.stereotype.Component;

@Component
public class DerivedNounFromAdjectiveKusSplitter extends AbstractDerivedNounFromAdjectiveSplitter {

    public DerivedNounFromAdjectiveKusSplitter(FormRepository formRepository) {
        super(formRepository);
    }

    @Override
    protected String getSuffix() {
        return "kus";
    }

    @Override
    protected boolean formTypeMatches(Form form) {
        var ekiRepresentation = form.getFormTypeCombination().getEkiRepresentation();
        return ekiRepresentation.equals("SgN");
    }

    @Override
    protected String getBase(String representation) {
        return representation.substring(0, representation.length() - getSuffix().length());
    }


}