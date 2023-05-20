package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.SplitCandidate;
import ee.mkv.estonian.repository.CompoundWordRepository;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.service.SplitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.*;
import java.util.stream.Collectors;

@Component
@CommandLine.Command(name = "split-words")
@RequiredArgsConstructor
@Slf4j
public class SplitCommand extends HelpAwarePicocliCommand {
    private final LexemeRepository lexemeRepository;
    private final SplitService splitService;
    private final FormRepository formRepository;
    private final CompoundWordRepository compoundWordRepository;

    private static void logCandidatesMap(Map<Lexeme, List<List<SplitCandidate>>> candidatesMap) {
        for (Map.Entry<Lexeme, List<List<SplitCandidate>>> entry : candidatesMap.entrySet()) {
            log.info("Lexeme found: {}", entry.getKey());
        }
    }

    private List<CompoundWord> determineMatchGroup(Lexeme lexeme, List<SplitCandidate> candidates, Map<String, List<Form>> candidateToFormMapping) {
        if (candidates.size() > 2) {
            // Hyphenation must be implemented differently
            log.error("Not implemented yet! Candidates: {}", candidates);
            return Collections.emptyList();
        }

        var result = new ArrayList<CompoundWord>(5);
        var firstComponent = candidates.get(0);
        for (Form form1 : candidateToFormMapping.get(firstComponent.getComponent())) {
            determineRuleByFirstComponentType(form1.getFormTypeCombination().getEkiRepresentation())
                    .ifPresentOrElse(compoundRule -> {
                        final SplitCandidate secondComponent = candidates.get(1);
                        for (Form form2 : candidateToFormMapping.get(secondComponent.getComponent())) {
                            if ("SgN".equals(form2.getFormTypeCombination().getEkiRepresentation())) {
                                result.add(buildCompoundForm(lexeme, buildPairOfCandidates(firstComponent, secondComponent, form1, form2), compoundRule));
                            } else {
                                log.warn("Unsupported component form for second component found: {}", form2);
                            }
                        }
                    }, () -> log.warn("Unsupported component form for first component found: {}", form1));
        }
        return result;
    }

    private List<CompoundWordComponent> buildPairOfCandidates(SplitCandidate firstComponent, SplitCandidate secondComponent, Form form1, Form form2) {
        return List.of(
                buildCompoundWordCandidate(firstComponent, form1, 0),
                buildCompoundWordCandidate(secondComponent, form2, 1)
        );
    }

    private CompoundWordComponent buildCompoundWordCandidate(SplitCandidate component, Form form, int index) {
        var result = new CompoundWordComponent();
        result.setForm(form);
        result.setComponentIndex(index);
        result.setComponentStartsAt(component.getStartsAt());
        return result;
    }

    private CompoundWord buildCompoundForm(Lexeme lexeme, List<CompoundWordComponent> components, CompoundRule compoundRule) {
        var result = new CompoundWord();
        result.setLexeme(lexeme);
        result.setCompoundRule(compoundRule);
        result.setComponents(components);
        for (CompoundWordComponent component : components) {
            component.setCompoundWord(result);
        }
        return result;
    }

    private Optional<CompoundRule> determineRuleByFirstComponentType(String ekiRepresentation) {
        switch (ekiRepresentation) {
            case "SgN":
                return Optional.of(CompoundRule.COMPOUND_OF_TWO_FIRST_SINGLE_NOMINATIVE);
            case "SgG":
                return Optional.of(CompoundRule.COMPOUND_OF_TWO_FIRST_SINGLE_GENITIVE);
            case "PlN":
                return Optional.of(CompoundRule.COMPOUND_OF_TWO_FIRST_PLURAL_NOMINATIVE);
            case "PlG":
                return Optional.of(CompoundRule.COMPOUND_OF_TWO_FIRST_PLURAL_GENITIVE);
            default:
                return Optional.empty();
        }
    }

    private Map<String, List<Form>> buildMapOfFormCandidates(List<Form> candidateForms) {
        Map<String, List<Form>> result = new HashMap<>();
        for (Form form : candidateForms) {
            String representation = form.getRepresentation().getRepresentation();
            if (result.containsKey(representation)) {
                result.get(representation).add(form);
            } else {
                var newList = new ArrayList<Form>();
                newList.add(form);
                result.put(representation, newList);
            }
        }
        return result;
    }

    private List<List<SplitCandidate>> produceCandidates(Lexeme lexeme) {
        String lemma = lexeme.getLemma().getRepresentation();
        if (lemma.contains("-")) {
            return splitService.splitByHyphen(lemma);
        }
        return splitService.splitNoHyphen(lemma);
    }

    private static void logCandidateForms(List<Form> candidateForms) {
        log.info("Found {} following candidate forms:", candidateForms.size());
        for (Form form : candidateForms) {
            log.info("{} {} {} {}", form.getId(), form.getLexeme().getId(), form.getRepresentation().getRepresentation(), form.getFormTypeCombination().getEkiRepresentation());
        }
    }

    @Override
    public ExitStatus call() throws Exception {
        boolean foundNewCompounds = true;
        while (foundNewCompounds) {
            foundNewCompounds = false;
            Map<Lexeme, List<List<SplitCandidate>>> candidatesMap = new HashMap<>();
            for (Lexeme lexeme : lexemeRepository.findNextUnsplitCandidates(1, 1)) {
                candidatesMap.put(lexeme, produceCandidates(lexeme));
            }

            logCandidatesMap(candidatesMap);

            var componentCandidates = candidatesMap.values().stream()
                    .flatMap(List::stream)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            log.info("Added {}", componentCandidates);
            log.info("Overall {} candidates found", componentCandidates.size());

            var candidateRepresentations = componentCandidates
                    .stream()
                    .map(SplitCandidate::getComponent)
                    .collect(Collectors.toSet());
            List<Form> candidateForms = formRepository.findWhereRepresentationIn(candidateRepresentations);
            logCandidateForms(candidateForms);

            Map<String, List<Form>> candidateToFormMapping = buildMapOfFormCandidates(candidateForms);

            for (Lexeme lexeme : candidatesMap.keySet()) {
                var splitCandidates = candidatesMap.get(lexeme);
                for (List<SplitCandidate> candidateList : splitCandidates) {
                    var goodCandidateCount = candidateList.stream()
                            .map(SplitCandidate::getComponent)
                            .filter(candidateToFormMapping::containsKey)
                            .count();
                    if (goodCandidateCount == candidateList.size()) {
                        log.info("All components found for lexeme {} and components {}", lexeme.getLemma().getRepresentation(), candidateList);
                        var compoundWords = determineMatchGroup(lexeme, candidateList, candidateToFormMapping);
                        log.info("CompoundWords: {} for lexeme {} and candidates {}", compoundWords, lexeme, candidateList);
                        if (!compoundWords.isEmpty()) {
                            compoundWordRepository.saveAll(compoundWords);
                            compoundWordRepository.flush();
                            foundNewCompounds = true;
                        }
                    }
                }

            }

            if (!foundNewCompounds) {
                log.warn("Not found any components for these lexemes: {}", candidatesMap.keySet());
            }
        }
        return ExitStatus.OK;
    }

}
