package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.Constants;
import ee.mkv.estonian.domain.Form;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class SplitUtils {
    private static final Set<String> SUITABLE_FTC_FOR_NON_LAST_COMPONENTS = Set.of("SgN", "SgG", "PlN", "PlG",
            Constants.IMMUTABLE_FORM, "RSgG");

    public boolean canBeNonLastComponentOfName(Form form) {
        return SUITABLE_FTC_FOR_NON_LAST_COMPONENTS.contains(form.getFormTypeCombination().getEkiRepresentation());
    }


}
