package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class LaneParadigmRestorer extends AbstractNameParadigmRestorer {

    private static final Map<FormTypeCombinationEnum, List<String>> ENDINGS = new EnumMap<>(FormTypeCombinationEnum.class);

    static {
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE, List.of("lase"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_PARTITIVE, List.of("last"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADITIVE, List.of("lasse"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ILLATIVE, List.of("lasesse"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_INESSIVE, List.of("lases"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ELATIVE, List.of("lasest"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ALLATIVE, List.of("lasele"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADESSIVE, List.of("lasel"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABLATIVE, List.of("laselt"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, List.of("laseks"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, List.of("laseni"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ESSIVE, List.of("lasena"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABESSIVE, List.of("laseta"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_COMITATIVE, List.of("lasega"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, List.of("las"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_NOMINATIVE, List.of("lased"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_GENITIVE, List.of("laste"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_PARTITIVE, List.of("lasi"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, List.of("lastesse", "lasisse"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, List.of("lastes", "lasis"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, List.of("lastest", "lasist"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, List.of("lastele", "lasile"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, List.of("lastel", "lasil"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, List.of("lastelt", "lasilt"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, List.of("lasteks", "lasiks"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TERMINATIVE, List.of("lasteni"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ESSIVE, List.of("lastena"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABESSIVE, List.of("lasteta"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_COMITATIVE, List.of("lastega"));
        ENDINGS.put(FormTypeCombinationEnum.ROOT_PLURAL, List.of("lasi"));
    }

    @Override
    protected Map<FormTypeCombinationEnum, List<String>> getMyEndings() {
        return ENDINGS;
    }

    @Override
    public String getInflectionType() {
        return "12i";
    }

    @Override
    protected String getMyEnding() {
        return "lane";
    }

}
