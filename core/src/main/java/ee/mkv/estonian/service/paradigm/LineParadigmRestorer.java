package ee.mkv.estonian.service.paradigm;


import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class LineParadigmRestorer extends AbstractNameParadigmRestorer {

    private static final EnumMap<FormTypeCombinationEnum, List<String>> ENDINGS = new EnumMap<>(FormTypeCombinationEnum.class);

    static {
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE, List.of("lise"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_PARTITIVE, List.of("list"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADITIVE, List.of("lisse"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ILLATIVE, List.of("lisesse"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_INESSIVE, List.of("lises"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ELATIVE, List.of("lisest"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ALLATIVE, List.of("lisele"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADESSIVE, List.of("lisel"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABLATIVE, List.of("liselt"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, List.of("liseks"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, List.of("liseni"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ESSIVE, List.of("lisena"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABESSIVE, List.of("liseta"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_COMITATIVE, List.of("lisega"));
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, List.of("lis"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_NOMINATIVE, List.of("lised"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_GENITIVE, List.of("liste"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_PARTITIVE, List.of("lisi"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, List.of("listesse", "lisisse"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, List.of("listes", "lisis"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, List.of("listest", "lisist"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, List.of("listele", "lisile"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, List.of("listel", "lisil"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, List.of("listelt", "lisilt"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, List.of("listeks", "lisiks"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TERMINATIVE, List.of("listeni"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ESSIVE, List.of("listena"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABESSIVE, List.of("listeta"));
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_COMITATIVE, List.of("listega"));
        ENDINGS.put(FormTypeCombinationEnum.ROOT_PLURAL, List.of("lisi"));
    }

    @Override
    protected String getMyEnding() {
        return "line";
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
