package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.model.FormTypeCombinationEnum;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class AbstractNameParadigmRestorer implements ParadigmRestorer {
    protected static final Set<EkiPartOfSpeech> NAMES = Set.of(EkiPartOfSpeech.NOUN, EkiPartOfSpeech.ADJECTIVE);

    @Override
    public Map<FormTypeCombinationEnum, List<String>> restoreParadigm(String baseForm) {
        ParadigmAccumulator paradigmAccumulator = new ParadigmAccumulator();
        EndingReplacer endingReplacer = new EndingReplacer(baseForm);
        paradigmAccumulator.accept(FormTypeCombinationEnum.SINGULAR_NOMINATIVE, baseForm);

        BiConsumer<FormTypeCombinationEnum, String> ar = (ftc, ending) -> paradigmAccumulator.accept(ftc, endingReplacer.replaceWith(ending));

        for (Map.Entry<FormTypeCombinationEnum, String> entry : getMyEndings().entrySet()) {
            ar.accept(entry.getKey(), entry.getValue());
        }

        return paradigmAccumulator.build();
    }

    protected abstract Map<FormTypeCombinationEnum, String> getMyEndings();

    @Override
    public boolean isMyParadigm(String baseForm, EkiPartOfSpeech partOfSpeech) {
        return NAMES.contains(partOfSpeech) && baseForm.endsWith(getMyEnding());
    }

    protected abstract String getMyEnding();

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
            this.root = baseForm.substring(0, baseForm.length() - getMyEnding().length());
        }

        private String replaceWith(String ending) {
            return root + ending;
        }
    }
}
