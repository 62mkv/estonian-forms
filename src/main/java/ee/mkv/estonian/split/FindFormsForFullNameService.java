package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.split.domain.Splitting;
import ee.mkv.estonian.split.domain.WordComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindFormsForFullNameService {

    private static final Set<String> SUITABLE_FTC_FOR_NON_LAST_COMPONENTS = Set.of("SgN", "SgG", "PlN", "PlG", "pf");
    private final FormRepository formRepository;
    private final WordSplitService wordSplitService;

    public Optional<CompoundWord> findFormsForSplittings(Lexeme lexeme) {
        var word = lexeme.getLemma().getRepresentation();
        var components = internalFindForms(word, true);
        if (components.isEmpty()) {
            return Optional.empty();
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

    private List<CompoundWordComponent> internalFindForms(String word, boolean isFullName) {
        var splittings = wordSplitService.findAllSplittings(word);
        var representationCandidates = splittings.stream()
                .flatMap(this::splittingToStrings)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        var forms = formRepository.findWhereRepresentationIn(representationCandidates);

        log.info("Found forms: {}", forms);

        var mapOfSplittingsToForms = splittings.stream()
                .collect(Collectors.toMap(
                        splitting -> splitting,
                        splitting -> findFormsForSplitting(splitting, forms)
                ))
                .entrySet().stream()
                .filter(this::hasMatchesForFinalComponent)
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
                return Collections.emptyList();
            }

        }

        log.info("Found forms for splittings: {}", filteredMapOfSplittingsToForms);

        var entries = filteredMapOfSplittingsToForms.entrySet().stream().sorted(this::orderByLongestFinalComponent).collect(Collectors.toList());
        for (var entry : entries) {
            var splitting = entry.getKey();
            var lastComponent = splitting.findLastComponent();
            var formsForFinalComponent = entry.getValue().get(lastComponent);
            var leftover = word.substring(0, lastComponent.getStartIndex());
            log.info("Leftover: {}", leftover);
            var formsForLeftover = internalFindForms(leftover, false);
            log.info("Forms for leftover: {}", formsForLeftover);
            if (!formsForLeftover.isEmpty()) {
                return combineComponents(formsForLeftover, lastComponent, formsForFinalComponent);
            }
        }
        return Collections.emptyList();
    }

    private List<CompoundWordComponent> combineComponents(List<CompoundWordComponent> componentsForLeftover, WordComponent lastComponent, List<Form> formsForFinalComponent) {
        var result = new ArrayList<>(componentsForLeftover);
        var lastLeftoverComponentIndex = componentsForLeftover
                .stream()
                .map(CompoundWordComponent::getComponentIndex)
                .max(Comparator.comparing(Integer::intValue))
                .get();
        var finalComponent = new CompoundWordComponent();
        finalComponent.setForm(formsForFinalComponent.get(0));
        finalComponent.setComponentIndex(lastLeftoverComponentIndex + 1);
        finalComponent.setComponentStartsAt(lastComponent.getStartIndex());
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
                    .collect(Collectors.toList());
            if (!filteredForms.isEmpty()) {
                result.put(component, filteredForms);
            }
        }
        return result;
    }

    private List<CompoundWordComponent> translateResults(Splitting splitting, Map<WordComponent, List<Form>> componentListMap) {
        var result = new ArrayList<CompoundWordComponent>();
        for (WordComponent component : splitting.getComponents()) {
            var forms = componentListMap.get(component);
            if (forms.isEmpty()) {
                return Collections.emptyList(); // this should not happen though
            }
            var compoundWordComponent = new CompoundWordComponent();
            compoundWordComponent.setForm(forms.get(0)); // because we've already filtered suitable forms, we know it's safe to pick any
            compoundWordComponent.setComponentIndex(component.getPosition());
            compoundWordComponent.setComponentStartsAt(component.getStartIndex());
            result.add(compoundWordComponent);
        }

        return result;
    }

    private boolean canBeLastComponentOfName(Form form) {
        return form.getFormTypeCombination().getFormTypes()
                .stream()
                .map(FormType::getEkiRepresentation)
                .collect(Collectors.toSet())
                .contains("N");
    }

    private boolean canBeNonLastComponentOfName(Form form) {
        return SUITABLE_FTC_FOR_NON_LAST_COMPONENTS.contains(form.getFormTypeCombination().getEkiRepresentation());
    }

    private boolean hasMatchesForFinalComponent(Map.Entry<Splitting, Map<WordComponent, List<Form>>> entry) {
        var componentFormsMap = entry.getValue();
        var lastComponent = entry.getKey().findLastComponent();

        return !componentFormsMap.get(lastComponent).isEmpty();
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
                .collect(Collectors.toList());
    }

    private Stream<String> splittingToStrings(Splitting splitting) {
        return splitting.getComponents().stream().map(WordComponent::getComponent);
    }


}
