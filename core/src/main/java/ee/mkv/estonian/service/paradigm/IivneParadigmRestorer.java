package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class IivneParadigmRestorer extends AbstractNameParadigmRestorer {

    private static final Map<FormTypeCombinationEnum, List<String>> ENDINGS = new EnumMap<>(FormTypeCombinationEnum.class);

    static {
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_NOMINATIVE, List.of("iivne"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE, List.of("iivse"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_PARTITIVE, List.of("iivset"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ILLATIVE, List.of("iivsesse"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_INESSIVE, List.of("iivses"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ELATIVE, List.of("iivsest"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ALLATIVE, List.of("iivsele"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADESSIVE, List.of("iivsel"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABLATIVE, List.of("iivselt"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, List.of("iivseks"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, List.of("iivseni"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ESSIVE, List.of("iivsena"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABESSIVE, List.of("iivseta"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_COMITATIVE, List.of("iivsega"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, List.of("iiv"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_NOMINATIVE, List.of("iivsed"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_GENITIVE, List.of("iivsete"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_PARTITIVE, List.of("iivseid"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, List.of("iivsetesse", "iivseisse"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, List.of("iivsetes", "iivseis"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, List.of("iivsetest", "iivseist"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, List.of("iivsetele", "iivseile"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, List.of("iivsetel", "iivseil"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, List.of("iivsetelt", "iivseilt"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, List.of("iivseteks", "iivseiks"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TERMINATIVE, List.of("iivseteni"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ESSIVE, List.of("iivsetena"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABESSIVE, List.of("iivseteta"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_COMITATIVE, List.of("iivsetega"));
        ENDINGS.put(FormTypeCombinationEnum.ROOT_PLURAL, List.of("iivsei"));
    }

    @Override
    protected String getMyEnding() {
        return "iivne";
    }

    @Override
    protected Map<FormTypeCombinationEnum, List<String>> getMyEndings() {
        return ENDINGS;
    }

    @Override
    public String getInflectionType() {
        return "2";
    }

}
