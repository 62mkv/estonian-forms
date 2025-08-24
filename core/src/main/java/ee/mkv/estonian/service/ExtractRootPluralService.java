package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.EkilexForm;
import ee.mkv.estonian.domain.EkilexParadigm;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.error.DuplicateParadigmFoundException;
import ee.mkv.estonian.model.DiscrepancyProjection;
import ee.mkv.estonian.repository.EkilexFormRepository;
import ee.mkv.estonian.repository.EkilexParadigmRepository;
import ee.mkv.estonian.repository.FormTypeCombinationRepository;
import ee.mkv.estonian.repository.RepresentationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExtractRootPluralService {

    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final RepresentationRepository representationRepository;
    private final EkilexFormRepository ekilexFormRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private FormTypeCombination formTypeCombination;

    public void extractRootPlural() {
        this.formTypeCombination = formTypeCombinationRepository.findByEkiRepresentation("Rpl").orElseThrow();

        while (true) {
            Set<Long> uniqueParadigms = new HashSet<>();
            final Set<DiscrepancyProjection> nextCandidatesForRootPlural = ekilexParadigmRepository.findNextCandidatesForRootPlural()
                    .stream()
                    .filter(p -> !uniqueParadigms.contains(p.getId()))
                    .peek(p -> uniqueParadigms.add(p.getId()))
                    .collect(Collectors.toSet());

            if (nextCandidatesForRootPlural.isEmpty()) {
                break;
            }

            List<Long> ids = nextCandidatesForRootPlural.stream().map(DiscrepancyProjection::getId).toList();
            var paradigms = new HashSet<>(ids);

            if (paradigms.size() < ids.size()) {
                var map = ids.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                log.error("Found duplicated paradigm in results: {}", map);
                throw new DuplicateParadigmFoundException(paradigms);
            }
            for (DiscrepancyProjection discrepancyProjection : nextCandidatesForRootPlural) {
                String inflected = discrepancyProjection.getInflected();
                Long paradigmId = discrepancyProjection.getId();
                log.info("Found paradigm {} with discrepancy {} ", paradigmId, inflected);

                var representation = addRepresentation(getRootFormFromInflected(inflected));
                var newForm = newRootPluralForm(paradigmId, representation);

                log.info("Added new RPl form: paradigm {}, representation {}", paradigmId, newForm.getRepresentation().getRepresentation());
            }
        }
    }

    private EkilexForm newRootPluralForm(Long paradigmId, Representation representation) {
        var ekilexForm = new EkilexForm();
        EkilexParadigm paradigm = new EkilexParadigm();
        paradigm.setId(paradigmId);
        ekilexForm.setEkilexParadigm(paradigm);
        ekilexForm.setRepresentation(representation);
        ekilexForm.setFormTypeCombination(this.formTypeCombination);
        return ekilexFormRepository.save(ekilexForm);
    }

    private Representation addRepresentation(String word) {
        return representationRepository.findByRepresentation(word)
                .orElseGet(() -> {
                    var representation = new Representation();
                    representation.setRepresentation(word);
                    return representationRepository.save(representation);
                });
    }

    private String getRootFormFromInflected(String inflected) {
        int len = inflected.length();
        return inflected.substring(0, len - 2);
    }

}
