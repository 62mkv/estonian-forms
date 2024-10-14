package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class IivneParadigmRestorer extends AbstractNameParadigmRestorer {

    private static final Map<FormTypeCombinationEnum, String> ENDINGS = new EnumMap<>(FormTypeCombinationEnum.class);

    static {
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_NOMINATIVE, "iivne");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE, "iivse");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_PARTITIVE, "iivset");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ILLATIVE, "iivsesse");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ILLATIVE, "iivsesse");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_INESSIVE, "iivses");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ELATIVE, "iivsest");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ALLATIVE, "iivsele");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ADESSIVE, "iivsel");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABLATIVE, "iivselt");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, "iivseks");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, "iivseni");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ESSIVE, "iivsena");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_ABESSIVE, "iivseta");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_COMITATIVE, "iivsega");
        ENDINGS.put(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, "iiv");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_NOMINATIVE, "iivsed");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_GENITIVE, "iivsete");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_PARTITIVE, "iivseid");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, "iivsetesse");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ILLATIVE, "iivseisse");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, "iivsetes");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_INESSIVE, "iivseis");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, "iivsetest");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ELATIVE, "iivseist");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, "iivsetele");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ALLATIVE, "iivseile");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, "iivsetel");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ADESSIVE, "iivseil");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, "iivsetelt");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABLATIVE, "iivseilt");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "iivseteks");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "iivseiks");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_TERMINATIVE, "iivseteni");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ESSIVE, "iivsetena");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_ABESSIVE, "iivseteta");
        ENDINGS.put(FormTypeCombinationEnum.PLURAL_COMITATIVE, "iivsetega");
        ENDINGS.put(FormTypeCombinationEnum.ROOT_PLURAL, "iivsei");
    }

    @Override
    protected String getMyEnding() {
        return "iivne";
    }

    @Override
    protected Map<FormTypeCombinationEnum, String> getMyEndings() {
        return ENDINGS;
    }

    @Override
    public String getInflectionType() {
        return "2";
    }

}
