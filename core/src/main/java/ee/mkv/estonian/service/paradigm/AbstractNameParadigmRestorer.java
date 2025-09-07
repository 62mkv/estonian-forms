package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.model.FormTypeCombinationEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@Slf4j
public abstract class AbstractNameParadigmRestorer implements ParadigmRestorer {
    protected static final Set<EkiPartOfSpeech> NAMES = Set.of(EkiPartOfSpeech.NOUN, EkiPartOfSpeech.ADJECTIVE);

    @Override
    public Map<FormTypeCombinationEnum, List<String>> restoreParadigm(String baseForm) {
        ParadigmAccumulator paradigmAccumulator = new ParadigmAccumulator();
        EndingReplacer endingReplacer = new EndingReplacer(baseForm);
        paradigmAccumulator.accept(FormTypeCombinationEnum.SINGULAR_NOMINATIVE, List.of(baseForm));

        BiConsumer<FormTypeCombinationEnum, List<String>> ar =
                (ftc, ending) -> paradigmAccumulator.accept(ftc, endingReplacer.replaceWith(ending));

        for (var entry : getMyEndings().entrySet()) {
            ar.accept(entry.getKey(), entry.getValue());
        }

        return paradigmAccumulator.build();
    }

    protected abstract Map<FormTypeCombinationEnum, List<String>> getMyEndings();

    @Override
    public boolean isMyParadigm(String baseForm, EkiPartOfSpeech partOfSpeech) {
        log.info("Checking if {}:{} is my paradigm", baseForm, partOfSpeech);
        var contains = NAMES.contains(partOfSpeech);
        var endsWith = baseForm.endsWith(getMyEnding());
        log.info("Contains: {}, endsWith: {}", contains, endsWith);
        return contains && endsWith;
    }

    protected abstract String getMyEnding();

    private static class ParadigmAccumulator {
        private final Map<FormTypeCombinationEnum, List<String>> result = new EnumMap<>(FormTypeCombinationEnum.class);

        private void accept(FormTypeCombinationEnum formTypeCombination, List<String> forms) {
            if (result.containsKey(formTypeCombination)) {
                result.get(formTypeCombination).addAll(forms);
            } else {
                result.put(formTypeCombination, forms);
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

        private List<String> replaceWith(List<String> ending) {
            return ending.stream()
                    .map(e -> root + e)
                    .toList();
        }
    }
}
