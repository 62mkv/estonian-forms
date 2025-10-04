package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@UtilityClass
@Slf4j
public class SplitUtils {
    private static final Set<String> SUITABLE_FTC_FOR_NON_LAST_COMPONENTS = Set.of("SgN", "SgG", "PlN", "PlG",
            Constants.IMMUTABLE_FORM, "RSgG", "SgEl", "PtsPrPs");

    public boolean canBeNonLastComponentOfName(Form form) {
        var ekiRepresentation = form.getFormTypeCombination().getEkiRepresentation();
        log.info("Checking if form {} with FTC {} can be non-last component of name", form, ekiRepresentation);
        return SUITABLE_FTC_FOR_NON_LAST_COMPONENTS.contains(ekiRepresentation);
    }

    public static CompoundWord getCompoundWord(Lexeme lexeme, Form form, CompoundRule compoundRule) {
        var result = new CompoundWord();
        result.setCompoundRule(compoundRule);
        result.setLexeme(lexeme);
        var component = new CompoundWordComponent();
        component.setForm(form);
        component.setComponentIndex(0);
        component.setComponentStartsAt(0);
        component.setCompoundWord(result);
        result.setComponents(List.of(component));
        return result;
    }

}
