package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.domain.EkilexForm;
import ee.mkv.estonian.domain.EkilexParadigm;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.model.DiscrepancyProjection;
import ee.mkv.estonian.repository.EkilexFormRepository;
import ee.mkv.estonian.repository.EkilexParadigmRepository;
import ee.mkv.estonian.repository.FormTypeCombinationRepository;
import ee.mkv.estonian.repository.RepresentationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;

@Component
@CommandLine.Command(name = "extract-rpl")
@RequiredArgsConstructor
@Slf4j
public class ExtractRootPluralCommand extends HelpAwarePicocliCommand {

    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final RepresentationsRepository representationRepository;
    private final EkilexFormRepository ekilexFormRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private FormTypeCombination formTypeCombination;

    @Override
    public ExitStatus call() throws Exception {
        this.formTypeCombination = formTypeCombinationRepository.findByEkiRepresentation("Rpl").orElseThrow();

        final List<DiscrepancyProjection> nextCandidatesForRootPlural = ekilexParadigmRepository.findNextCandidatesForRootPlural()
                .toList();
        for (DiscrepancyProjection discrepancyProjection : nextCandidatesForRootPlural) {
            String inflected = discrepancyProjection.getInflected();
            Long paradigm = discrepancyProjection.getId();
            log.info("Found paradigm {} with discrepancy {} ", paradigm, inflected);

            var representation = addRepresentation(getRootFormFromInflected(inflected));
            var newForm = newRootPluralForm(paradigm, representation);

            log.info("Added new RPl form: paradigm {}, representation {}", newForm.getEkilexParadigm().getId(), newForm.getRepresentation().getRepresentation());
        }

        return ExitStatus.OK;
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
