package ee.mkv.estonian.service.paradigm;


import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class LineParadigmRestorer extends AbstractNameParadigmRestorer {

    private static final EnumMap<FormTypeCombinationEnum, String> ENDINGS = new EnumMap<>(FormTypeCombinationEnum.class);

    static {
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE, "lise");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_PARTITIVE, "list");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADITIVE, "lisse");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ILLATIVE, "lisesse");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_INESSIVE, "lises");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ELATIVE, "lisest");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ALLATIVE, "lisele");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADESSIVE, "lisel");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABLATIVE, "liselt");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, "liseks");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, "liseni");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ESSIVE, "lisena");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABESSIVE, "liseta");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_COMITATIVE, "lisega");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, "lis");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_NOMINATIVE, "lised");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_GENITIVE, "liste");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_PARTITIVE, "lisi");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, "listesse");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, "lisisse");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, "listes");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, "lisis");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, "listest");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, "lisist");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, "listele");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, "lisile");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, "listel");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, "lisil");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, "listelt");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, "lisilt");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "listeks");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "lisiks");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TERMINATIVE, "listeni");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ESSIVE, "listena");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABESSIVE, "listeta");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_COMITATIVE, "listega");
        ENDINGS.put(FormTypeCombinationEnum.ROOT_PLURAL, "lisi");
    }

    @Override
    protected String getMyEnding() {
        return "line";
    }

    @Override
    protected Map<FormTypeCombinationEnum, String> getMyEndings() {
        return ENDINGS;
    }

    @Override
    public String getInflectionType() {
        return "12i";
    }
}
