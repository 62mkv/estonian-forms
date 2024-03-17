package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class LaneParadigmRestorer extends AbstractNameParadigmRestorer {

    private static final Map<FormTypeCombinationEnum, String> ENDINGS = new EnumMap<>(FormTypeCombinationEnum.class);

    static {
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE, "lase");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_PARTITIVE, "last");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADITIVE, "lasse");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ILLATIVE, "lasesse");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_INESSIVE, "lases");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ELATIVE, "lasest");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ALLATIVE, "lasele");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADESSIVE, "lasel");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABLATIVE, "laselt");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, "laseks");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, "laseni");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ESSIVE, "lasena");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABESSIVE, "laseta");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_COMITATIVE, "lasega");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, "las");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_NOMINATIVE, "lased");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_GENITIVE, "laste");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_PARTITIVE, "lasi");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, "lastesse");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, "lasisse");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, "lastes");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, "lasis");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, "lastest");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, "lasist");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, "lastele");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, "lasile");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, "lastel");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, "lasil");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, "lastelt");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, "lasilt");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "lasteks");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "lasiks");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TERMINATIVE, "lasteni");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ESSIVE, "lastena");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABESSIVE, "lasteta");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_COMITATIVE, "lastega");
        ENDINGS.put(FormTypeCombinationEnum.ROOT_PLURAL, "lasi");
    }

    @Override
    protected Map<FormTypeCombinationEnum, String> getMyEndings() {
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
