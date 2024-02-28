package ee.mkv.estonian.service.paradigm;


import ee.mkv.estonian.model.FormTypeCombinationEnum;
import ee.mkv.estonian.model.PartOfSpeechEnum;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;

@Component
public class LineParadigmRestorer implements ParadigmRestorer {
    private static final Set<PartOfSpeechEnum> NAMES = Set.of(PartOfSpeechEnum.NOUN, PartOfSpeechEnum.ADJECTIVE);

    @Override
    public Map<FormTypeCombinationEnum, List<String>> restoreParadigm(String baseForm) {
        ParadigmAccumulator paradigmAccumulator = new ParadigmAccumulator();
        EndingReplacer endingReplacer = new EndingReplacer(baseForm);
        paradigmAccumulator.accept(FormTypeCombinationEnum.SINGULAR_NOMINATIVE, baseForm);

        BiConsumer<FormTypeCombinationEnum, String> ar = (ftc, ending) -> paradigmAccumulator.accept(ftc, endingReplacer.replaceWith(ending));

        ar.accept(FormTypeCombinationEnum.SINGULAR_GENITIVE, "lise");
        ar.accept(FormTypeCombinationEnum.SINGULAR_PARTITIVE, "list");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ADITIVE, "lisse");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ILLATIVE, "lisesse");
        ar.accept(FormTypeCombinationEnum.SINGULAR_INESSIVE, "lises");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ELATIVE, "lisest");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ALLATIVE, "lisele");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ADESSIVE, "lisel");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ABLATIVE, "liselt");
        ar.accept(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE, "liseks");
        ar.accept(FormTypeCombinationEnum.SINGULAR_TERMINATIVE, "liseni");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ESSIVE, "lisena");
        ar.accept(FormTypeCombinationEnum.SINGULAR_ABESSIVE, "liseta");
        ar.accept(FormTypeCombinationEnum.SINGULAR_COMITATIVE, "lisega");
        ar.accept(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED, "lis");
        ar.accept(FormTypeCombinationEnum.PLURAL_NOMINATIVE, "lised");
        ar.accept(FormTypeCombinationEnum.PLURAL_GENITIVE, "liste");
        ar.accept(FormTypeCombinationEnum.PLURAL_PARTITIVE, "lisi");
        ar.accept(FormTypeCombinationEnum.PLURAL_ILLATIVE, "listesse");
        ar.accept(FormTypeCombinationEnum.PLURAL_ILLATIVE, "lisisse");
        ar.accept(FormTypeCombinationEnum.PLURAL_INESSIVE, "listes");
        ar.accept(FormTypeCombinationEnum.PLURAL_INESSIVE, "lisis");
        ar.accept(FormTypeCombinationEnum.PLURAL_ELATIVE, "listest");
        ar.accept(FormTypeCombinationEnum.PLURAL_ELATIVE, "lisist");
        ar.accept(FormTypeCombinationEnum.PLURAL_ALLATIVE, "listele");
        ar.accept(FormTypeCombinationEnum.PLURAL_ALLATIVE, "lisile");
        ar.accept(FormTypeCombinationEnum.PLURAL_ADESSIVE, "listel");
        ar.accept(FormTypeCombinationEnum.PLURAL_ADESSIVE, "lisil");
        ar.accept(FormTypeCombinationEnum.PLURAL_ABLATIVE, "listelt");
        ar.accept(FormTypeCombinationEnum.PLURAL_ABLATIVE, "lisilt");
        ar.accept(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "listeks");
        ar.accept(FormTypeCombinationEnum.PLURAL_TRANSLATIVE, "lisiks");
        ar.accept(FormTypeCombinationEnum.PLURAL_TERMINATIVE, "listeni");
        ar.accept(FormTypeCombinationEnum.PLURAL_ESSIVE, "listena");
        ar.accept(FormTypeCombinationEnum.PLURAL_ABESSIVE, "listeta");
        ar.accept(FormTypeCombinationEnum.PLURAL_COMITATIVE, "listega");
        ar.accept(FormTypeCombinationEnum.ROOT_PLURAL, "lisi");

        return paradigmAccumulator.build();
    }

    @Override
    public boolean isMyParadigm(String baseForm, PartOfSpeechEnum partOfSpeech) {
        return NAMES.contains(partOfSpeech) && baseForm.endsWith("line");
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
