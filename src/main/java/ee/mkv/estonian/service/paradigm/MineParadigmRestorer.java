package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;

@Component
public class MineParadigmRestorer implements ParadigmRestorer {
    private static final Set<EkiPartOfSpeech> NAMES = Set.of(EkiPartOfSpeech.NOUN, EkiPartOfSpeech.ADJECTIVE);

    @Override
    public Map<FormTypeCombinationEnum, List<String>> restoreParadigm(String baseForm) {
        ParadigmAccumulator paradigmAccumulator = new ParadigmAccumulator();
        EndingReplacer endingReplacer = new EndingReplacer(baseForm);
        paradigmAccumulator.accept(FormTypeCombinationEnum.SINGULAR_NOMINATIVE, baseForm);

        BiConsumer<FormTypeCombinationEnum, String> ar = (ftc, ending) -> paradigmAccumulator.accept(ftc, endingReplacer.replaceWith(ending));

        ar.accept(FormTypeCombinationEnum.SINGULAR_GENITIVE, "mise");
        ar.accept(FormTypeCombinationEnum.SINGULAR_PARTITIVE, "mist");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ADITIVE, "misse");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ILLATIVE, "misesse");
        ar.accept(FormTypeCombinationEnum.SINGULAR_INESSIVE, "mises");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ELATIVE, "misest");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ALLATIVE, "misele");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ADESSIVE, "misel");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ABLATIVE, "miselt");
        ar.accept(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, "miseks");
        ar.accept(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, "miseni");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ESSIVE, "misena");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ABESSIVE, "miseta");
        ar.accept(FormTypeCombinationEnum.SINGULAR_COMITATIVE, "misega");
        ar.accept(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, "mis");
        ar.accept(FormTypeCombinationEnum.PLURAL_NOMINATIVE, "mised");
        ar.accept(FormTypeCombinationEnum.PLURAL_GENITIVE, "miste");
        ar.accept(FormTypeCombinationEnum.PLURAL_PARTITIVE, "misi");
        ar.accept(FormTypeCombinationEnum.PLURAL_ILLATIVE, "mistesse");
        ar.accept(FormTypeCombinationEnum.PLURAL_ILLATIVE, "misisse");
        ar.accept(FormTypeCombinationEnum.PLURAL_INESSIVE, "mistes");
        ar.accept(FormTypeCombinationEnum.PLURAL_INESSIVE, "misis");
        ar.accept(FormTypeCombinationEnum.PLURAL_ELATIVE, "mistest");
        ar.accept(FormTypeCombinationEnum.PLURAL_ELATIVE, "misist");
        ar.accept(FormTypeCombinationEnum.PLURAL_ALLATIVE, "mistele");
        ar.accept(FormTypeCombinationEnum.PLURAL_ALLATIVE, "misile");
        ar.accept(FormTypeCombinationEnum.PLURAL_ADESSIVE, "mistel");
        ar.accept(FormTypeCombinationEnum.PLURAL_ADESSIVE, "misil");
        ar.accept(FormTypeCombinationEnum.PLURAL_ABLATIVE, "mistelt");
        ar.accept(FormTypeCombinationEnum.PLURAL_ABLATIVE, "misilt");
        ar.accept(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "misteks");
        ar.accept(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "misiks");
        ar.accept(FormTypeCombinationEnum.PLURAL_TERMINATIVE, "misteni");
        ar.accept(FormTypeCombinationEnum.PLURAL_ESSIVE, "mistena");
        ar.accept(FormTypeCombinationEnum.PLURAL_ABESSIVE, "misteta");
        ar.accept(FormTypeCombinationEnum.PLURAL_COMITATIVE, "mistega");
        ar.accept(FormTypeCombinationEnum.ROOT_PLURAL, "misi");

        return paradigmAccumulator.build();
    }

    @Override
    public boolean isMyParadigm(String baseForm, EkiPartOfSpeech partOfSpeech) {
        return NAMES.contains(partOfSpeech) && baseForm.endsWith("mine");
    }

    @Override
    public String getInflectionType() {
        return "12i";
    }

    private class ParadigmAccumulator {
        private final Map<FormTypeCombinationEnum, List<String>> result = new EnumMap<>(FormTypeCombinationEnum.class);

        private void accept(FormTypeCombinationEnum formTypeCombination, String form) {
            if (result.containsKey(formTypeCombination)) {
                result.get(formTypeCombination).add(form);
            } else {
                result.put(formTypeCombination, new ArrayList<>(List.of(form)));
            }
        }

        private Map<FormTypeCombinationEnum, List<String>> build() {
            return result;
        }
    }

    private class EndingReplacer {
        private final String root;

        private EndingReplacer(String baseForm) {
            this.root = baseForm.substring(0, baseForm.length() - 4);
        }

        private String replaceWith(String ending) {
            return root + ending;
        }
    }
}
