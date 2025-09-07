package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class IneParadigmRestorer extends AbstractNameParadigmRestorer {
    private static final EnumMap<FormTypeCombinationEnum, List<String>> ENDINGS = new EnumMap<>(FormTypeCombinationEnum.class);

    static {
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE, List.of("ise"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_PARTITIVE, List.of("ist"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADITIVE, List.of("isse"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ILLATIVE, List.of("isesse"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_INESSIVE, List.of("ises"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ELATIVE, List.of("isest"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ALLATIVE, List.of("isele"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADESSIVE, List.of("isel"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABLATIVE, List.of("iselt"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, List.of("iseks"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, List.of("iseni"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ESSIVE, List.of("isena"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABESSIVE, List.of("iseta"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_COMITATIVE, List.of("isega"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, List.of("is"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_NOMINATIVE, List.of("ised"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_GENITIVE, List.of("iste"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_PARTITIVE, List.of("isi"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, List.of("istesse", "isisse"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, List.of("istes", "isis"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, List.of("istest", "isist"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, List.of("istele", "isile"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, List.of("istel", "isil"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, List.of("istelt", "isilt"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, List.of("isteks", "isiks"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TERMINATIVE, List.of("isteni"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ESSIVE, List.of("istena"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABESSIVE, List.of("isteta"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_COMITATIVE, List.of("istega"));
        ENDINGS.put(FormTypeCombinationEnum.ROOT_PLURAL, List.of("isi"));
    }

    @Override
    protected String getMyEnding() {
        return "ine";
    }

    @Override
    protected Map<FormTypeCombinationEnum, List<String>> getMyEndings() {
        return ENDINGS;
    }

    @Override
    public String getInflectionType() {
        return "12i";
    }

}
