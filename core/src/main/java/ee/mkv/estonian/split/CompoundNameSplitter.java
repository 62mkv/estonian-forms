package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.service.UserInputProvider;
import ee.mkv.estonian.service.lexeme.LexemeInitializer;
import ee.mkv.estonian.split.domain.Splitting;
import ee.mkv.estonian.split.domain.WordComponent;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompoundNameSplitter implements LexemeSplitter {

    private static final Set<InternalPartOfSpeech> SUITABLE_PART_OF_SPEECH_FOR_NON_LAST_COMPONENTS = Set.of(
            InternalPartOfSpeech.ADJECTIVE,
            InternalPartOfSpeech.NOUN,
            InternalPartOfSpeech.PRONOUN,
            InternalPartOfSpeech.PREFIX,
            InternalPartOfSpeech.ADVERB
    );

    private final UserInputProvider userInputProvider;
    private final FormRepository formRepository;
    private final WordSplitService wordSplitService;
    private final LexemeInitializer lexemeInitializer;

    private static CompoundWordComponent buildCompoundWordComponent(int index, WordComponent component, List<Form> forms) {
        log.info("Building compound word component for component{}: {} with forms: {}", index, component, forms);
        var finalComponent = new CompoundWordComponent();
        finalComponent.setForm(IterableUtils.getFirstValueOrFail(forms));
        finalComponent.setComponentIndex(index);
        finalComponent.setComponentStartsAt(component.getStartIndex());
        return finalComponent;
    }

    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        var word = lexeme.getLemma().getRepresentation();
        var iteration = new SplitterIteration();
        var components = iteration.internalFindForms(word, true);
        if (components.isEmpty()) {
            if (hasChances(iteration)) {
                var components2 = new SplitterIteration().internalFindForms(word, true);
                if (components2.isEmpty()) {
                    return Optional.empty();
                } else {
                    components = components2;
                }
            } else {
                return Optional.empty();
            }
        }
        var compoundWord = new CompoundWord();
        compoundWord.setComponents(components);
        compoundWord.setCompoundRule(CompoundRule.COMPLEX_COMPOUND_WORD);
        compoundWord.setLexeme(lexeme);
        for (CompoundWordComponent component : components) {
            component.setCompoundWord(compoundWord);
        }
        return Optional.of(compoundWord);
    }

    @Override
    public int getPriority() {
        return 2;
    }

    private boolean hasChances(SplitterIteration iteration) {
        var leftovers = iteration.leftovers;
        log.info("Leftovers visited: {}", leftovers);
        boolean hasChance = false;
        for (var leftover : leftovers) {
            if (leftover.endsWith("mis")) {
                var candidate = leftover.substring(0, leftover.length() - 3) + "mine";
                try {
                    lexemeInitializer.initializeLexeme(candidate, InternalPartOfSpeech.NOUN);
                    hasChance = true;
                } catch (Exception e) {
                    log.error("Failed to initialize lexeme '{}'", candidate, e);
                }

            }
        }
        return hasChance;
    }

    private class SplitterIteration {
        private final List<String> leftovers = new ArrayList<>();

        private List<CompoundWordComponent> internalFindForms(String word, boolean isFullName) {
            var splittings = wordSplitService.findAllSplittings(word);
            var representationCandidates = splittings.stream()
                    .flatMap(this::splittingToStrings)
                    .distinct()
                    .collect(Collectors.toCollection(ArrayList::new));

            if (representationCandidates.isEmpty()) {
                log.warn("No representation candidates in splitting: {}", splittings);
                return Collections.emptyList();
            }

            log.info("Searching for forms for representation candidates: {}", representationCandidates);

            var forms = formRepository.findWhereRepresentationIn(representationCandidates);

            log.info("Found forms: {}", logsRepresentationFor(forms));

            if (splittings.size() == 1 && splittings.stream().allMatch(Splitting::isHyphenated)) {
                var splitting = splittings.stream().findFirst().get();
                if (hasMatchesForAllComponents(splitting, forms)) {
                    return leftoverStrategy(word, isFullName, splittings, forms);
                }
                var formsForSplitting = findFormsForSplitting(splitting, forms);
                if (hasMatchesForAllButFinalComponent(splitting, formsForSplitting)) {
                    final WordComponent lastComponent = splitting.findLastComponent();
                    var componentsForRightLeftover = internalFindForms(lastComponent.getComponent(), true);
                    if (!componentsForRightLeftover.isEmpty()) {
                        return combineComponentsWhenLeftoverIsToTheRight(splitting, formsForSplitting, componentsForRightLeftover);
                    }
                }
                return Collections.emptyList();
            }

            return leftoverStrategy(word, isFullName, splittings, forms);
        }

        private boolean hasMatchesForAllComponents(Splitting splitting, List<Form> forms) {
            final Map<WordComponent, List<Form>> formsForSplitting = findFormsForSplitting(splitting, forms);
            return hasMatchesForAllButFinalComponent(splitting, formsForSplitting)
                    && hasMatchesForFinalComponent(splitting, formsForSplitting);
        }

        private List<CompoundWordComponent> combineComponentsWhenLeftoverIsToTheRight(
                Splitting splitting,
                Map<WordComponent, List<Form>> formsForSplitting,
                List<CompoundWordComponent> componentsForRightLeftover) {
            log.info("Found forms for splitting: {}", logsRepresentationFor(formsForSplitting));
            log.info("Found components for right leftover: {}", componentsForRightLeftover);
            var result = new ArrayList<CompoundWordComponent>();
            var lastComponentWithForms = formsForSplitting.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(entry -> entry.getKey().getPosition()))
                    .filter(entry -> !entry.getValue().isEmpty())
                    .reduce((first, second) -> second)
                    .map(Map.Entry::getKey)
                    .get();

            var firstComponentOfLeftover = splitting.nextComponent(lastComponentWithForms);
            result.addAll(translateResults(splitting.upTo(lastComponentWithForms), formsForSplitting));
            result.addAll(patchRightLeftover(componentsForRightLeftover, firstComponentOfLeftover));

            final List<CompoundWordComponent> sortedByComponent = result.stream()
                    .sorted(Comparator.comparing(CompoundWordComponent::getComponentIndex))
                    .toList();
            for (CompoundWordComponent component : sortedByComponent) {
                log.info("Component: {}", component);
            }

            return result;
        }

        private String logsRepresentationFor(Map<WordComponent, List<Form>> formsForSplitting) {
            return formsForSplitting.entrySet()
                    .stream()
                    .reduce(new StringBuilder(), (builder, entry) -> {
                        builder.append(logsRepresentationFor(entry.getKey()))
                                .append(":")
                                .append(logsRepresentationFor(entry.getValue()))
                                .append("\\n");
                        return builder;
                    }, StringBuilder::append)
                    .toString();
        }

        private String logsRepresentationFor(List<Form> forms) {
            return forms.stream()
                    .reduce(new StringBuilder(), (builder, form) -> {
                        builder.append(logsRepresentationFor(form))
                                .append(";");
                        return builder;
                    }, StringBuilder::append)
                    .toString();
        }

        private String logsRepresentationFor(Form form) {
            assert form.getRepresentation() != null;
            assert form.getLexeme() != null;
            assert form.getFormTypeCombination() != null;
            assert form.getLexeme().getPartOfSpeech() != null;
            return String.format("form{%s:%s:%s}",
                    form.getRepresentation().getRepresentation(),
                    form.getLexeme().getPartOfSpeech().getPartOfSpeechName(),
                    form.getFormTypeCombination().getEkiRepresentation());
        }

        private String logsRepresentationFor(WordComponent wordComponent) {
            return String.format("wordComponent{%d/%d/%s}", wordComponent.getPosition(), wordComponent.getStartIndex(), wordComponent.getComponent());
        }

        private List<CompoundWordComponent> patchRightLeftover(List<CompoundWordComponent> componentsForRightLeftover,
                                                               WordComponent firstComponentOfLeftover) {
            return componentsForRightLeftover
                    .stream()
                    .map(component -> {
                        var newComponent = new CompoundWordComponent();
                        newComponent.setComponentIndex(firstComponentOfLeftover.getPosition() + component.getComponentIndex());
                        newComponent.setComponentStartsAt(firstComponentOfLeftover.getStartIndex() + component.getComponentStartsAt());
                        newComponent.setForm(component.getForm());
                        return newComponent;
                    })
                    .toList();
        }

        private String format(Splitting splitting) {
            return splitting.getComponents().stream()
                    .map(WordComponent::getComponent)
                    .collect(Collectors.joining(":"));
        }

        private List<CompoundWordComponent> leftoverStrategy(String word, boolean isFullName, Set<Splitting> splittings, List<Form> forms) {
            var mapOfSplittingsToForms = splittings.stream()
                    .collect(Collectors.toMap(
                            splitting -> splitting,
                            splitting -> findFormsForSplitting(splitting, forms)
                    ))
                    .entrySet().stream()
                    .filter(entry1 -> hasMatchesForFinalComponent(entry1.getKey(), entry1.getValue()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

            var filteredMapOfSplittingsToForms = mapOfSplittingsToForms.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> filterSuitableForms(entry.getKey(), entry.getValue(), isFullName)
                    ));

            var splittingWithAllMatches = findSplittingsWithAllMatches(filteredMapOfSplittingsToForms);

            if (!splittingWithAllMatches.isEmpty()) {

                if (splittingWithAllMatches.keySet().size() == 1) {
                    var splitting = splittingWithAllMatches.keySet().stream().findFirst().get();
                    final Map<WordComponent, List<Form>> componentListMap = splittingWithAllMatches.get(splitting);
                    log.info("Found splitting {} with forms for all components: {}", splitting, componentListMap);
                    return translateResults(splitting, componentListMap);
                } else {
                    log.info("Found multiple splittings with forms for all components: {}", splittingWithAllMatches);

                    AtomicInteger key = new AtomicInteger(0);
                    Map<Integer, Splitting> splittingsByIndex = splittingWithAllMatches.entrySet().stream()
                            .collect(Collectors.toMap(
                                    entry -> key.incrementAndGet(),
                                    Map.Entry::getKey
                            ));

                    for (Integer k : splittingsByIndex.keySet()) {
                        log.info("Splitting {}: {}", k, format(splittingsByIndex.get(k)));
                    }

                    // wait for user input
                    log.info("Please select splitting index: ");
                    int selectedSplittingIndex = userInputProvider.getUserChoice(
                            splittingsByIndex.keySet().stream()
                                    .map(Object::toString)
                                    .toArray(String[]::new)
                    );
                    log.info("You selected: {} {}", selectedSplittingIndex, format(splittingsByIndex.get(selectedSplittingIndex)));
                    var selectedSplitting = splittingsByIndex.get(selectedSplittingIndex);

                    return translateResults(selectedSplitting, splittingWithAllMatches.get(selectedSplitting));
                }

            }

            log.info("Found forms for splittings: {}", logsRepresentationForSplittings(filteredMapOfSplittingsToForms));

            var entries = filteredMapOfSplittingsToForms.entrySet().stream().sorted(this::orderByLongestFinalComponent)
                    .toList();
            for (var entry : entries) {
                var splitting = entry.getKey();
                var lastComponent = splitting.findLastComponent();
                var entryValue = entry.getValue();
                log.info("Splitting: {}", splitting);
                log.info("Forms for splitting: {}", entryValue);
                log.info("Last component: {}", lastComponent);
                var formsForFinalComponent = entryValue.get(lastComponent);
                if (formsForFinalComponent == null || formsForFinalComponent.isEmpty()) {
                    log.info("No forms found for final component: {} (probably dues to filtering)", lastComponent);
                    continue;
                }
                log.info("Forms for final component: {}", logsRepresentationFor(formsForFinalComponent));
                assert formsForFinalComponent != null && !formsForFinalComponent.isEmpty() : "Forms for final component should not be empty";
                var leftover = word.substring(0, lastComponent.getStartIndex());
                if (leftover.isEmpty()) {
                    continue;
                }
                log.info("Leftover: {}", leftover);
                leftovers.add(leftover);
                var formsForLeftover = internalFindForms(leftover, false);
                log.info("Forms for leftover: {}", formsForLeftover);
                if (!formsForLeftover.isEmpty()) {
                    return combineComponentsWhenLeftoverIsToTheLeft(formsForLeftover, lastComponent, formsForFinalComponent);
                }
            }

            return Collections.emptyList();
        }

        private String logsRepresentationForSplittings(Map<Splitting, Map<WordComponent, List<Form>>> filteredMapOfSplittingsToForms) {
            return filteredMapOfSplittingsToForms.entrySet()
                    .stream()
                    .reduce(new StringBuilder(), (builder, entry) -> {
                        builder.append(format(entry.getKey()))
                                .append(":")
                                .append(logsRepresentationFor(entry.getValue()))
                                .append(";");
                        return builder;
                    }, StringBuilder::append)
                    .toString();
        }

        private List<CompoundWordComponent> combineComponentsWhenLeftoverIsToTheLeft(List<CompoundWordComponent> componentsForLeftover, WordComponent lastComponent, List<Form> formsForFinalComponent) {
            assert formsForFinalComponent != null && !formsForFinalComponent.isEmpty() : "Forms for final component should not be empty";
            var result = new ArrayList<>(componentsForLeftover);
            var lastLeftoverComponentIndex = componentsForLeftover
                    .stream()
                    .map(CompoundWordComponent::getComponentIndex)
                    .max(Comparator.comparing(Integer::intValue))
                    .get();
            final var finalComponent = buildCompoundWordComponent(lastLeftoverComponentIndex + 1, lastComponent, formsForFinalComponent);
            result.add(finalComponent);
            return result;
        }

        private Map<WordComponent, List<Form>> filterSuitableForms(Splitting splitting, Map<WordComponent, List<Form>> componentListMap, boolean isFullName) {
            Map<WordComponent, List<Form>> result = new HashMap<>();
            var lastComponent = splitting.findLastComponent();
            for (WordComponent component : splitting.getComponents()) {
                var foundForms = componentListMap.get(component);
                Predicate<Form> filterCondition = isFullName && lastComponent.equals(component)
                        ? this::canBeLastComponentOfName
                        : this::canBeNonLastComponentOfName;

                final List<Form> filteredForms = foundForms.stream()
                        .filter(filterCondition)
                        .toList();
                if (!filteredForms.isEmpty()) {
                    result.put(component, filteredForms);
                }
            }
            return result;
        }

        private boolean canBeNonLastComponentOfName(Form form) {
            var partOfSpeech = InternalPartOfSpeech.fromEkiCodes(form.getLexeme().getPartOfSpeech().getEkiCodes());

            return SplitUtils.canBeNonLastComponentOfName(form) && SUITABLE_PART_OF_SPEECH_FOR_NON_LAST_COMPONENTS.contains(partOfSpeech)
                    || isExceptionGranted(form);
        }

        private boolean isExceptionGranted(Form form) {
            return form.getRepresentation().getRepresentation().equals("midagi");
        }

        private List<CompoundWordComponent> translateResults(Splitting splitting, Map<WordComponent, List<Form>> componentListMap) {
            var result = new ArrayList<CompoundWordComponent>();
            for (WordComponent component : splitting.getComponents()) {
                var forms = componentListMap.get(component);
                if (forms.isEmpty()) {
                    log.warn("No forms found for component: {} in splitting: {}", component, splitting);
                    return Collections.emptyList(); // this should not happen though
                }
                final var compoundWordComponent = buildCompoundWordComponent(component.getPosition(), component, forms);
                result.add(compoundWordComponent);
            }

            return result;
        }

        private boolean canBeLastComponentOfName(Form form) {
            boolean isNounForm = form.getFormTypeCombination().getFormTypes()
                    .stream()
                    .map(FormType::getEkiRepresentation)
                    .collect(Collectors.toSet())
                    .contains("N");
            boolean isParticiple = form.getFormTypeCombination().getEkiRepresentation().equals("PtsPtIps");
            return isNounForm || isParticiple;
        }

        private boolean hasMatchesForFinalComponent(Splitting splitting, Map<WordComponent, List<Form>> componentListMap) {
            var lastComponent = splitting.findLastComponent();

            return !componentListMap.get(lastComponent).isEmpty();
        }

        private boolean hasMatchesForAllButFinalComponent(Splitting splitting, Map<WordComponent, List<Form>> componentListMap) {
            return splitting.getComponents().stream()
                    .filter(component -> !component.equals(splitting.findLastComponent()))
                    .noneMatch(component -> componentListMap.get(component).isEmpty());
        }

        private int orderByLongestFinalComponent(Map.Entry<Splitting, Map<WordComponent, List<Form>>> entry,
                                                 Map.Entry<Splitting, Map<WordComponent, List<Form>>> entry1) {
            var component1 = entry.getKey().findLastComponent();
            var component2 = entry1.getKey().findLastComponent();
            return component2.getComponent().length() - component1.getComponent().length();
        }

        private Map<Splitting, Map<WordComponent, List<Form>>> findSplittingsWithAllMatches(Map<Splitting, Map<WordComponent, List<Form>>> mapOfSplittingsToForms) {
            return mapOfSplittingsToForms
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().getComponents().size() == getComponentsWithNotEmptyForms(entry.getValue()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
        }

        private int getComponentsWithNotEmptyForms(Map<WordComponent, List<Form>> entry) {
            return (int) entry.values().stream().filter(forms -> !forms.isEmpty()).count();
        }

        private Map<WordComponent, List<Form>> findFormsForSplitting(Splitting splitting, List<Form> forms) {
            return splitting.getComponents().stream()
                    .collect(Collectors.toMap(
                            component -> component,
                            component -> findFormsForComponent(component, forms)
                    ));
        }

        private List<Form> findFormsForComponent(WordComponent component, List<Form> forms) {
            return forms.stream()
                    .filter(form -> form.getRepresentation().getRepresentation().equals(component.getComponent()))
                    .toList();
        }

        private Stream<String> splittingToStrings(Splitting splitting) {
            return splitting.getComponents().stream().map(WordComponent::getComponent);
        }
    }
}
