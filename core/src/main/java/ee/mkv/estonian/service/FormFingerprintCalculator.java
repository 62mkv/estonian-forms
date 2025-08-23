package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Article;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.repository.FormTypeCombinationRepository;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FormFingerprintCalculator {

    private final FormTypeCombinationRepository formTypeCombinationRepository;

    public FormFingerprintCalculator(FormTypeCombinationRepository formTypeCombinationRepository) {
        this.formTypeCombinationRepository = formTypeCombinationRepository;
    }

    public String getFormFingerprint(Article article) {
        if (article.getPartOfSpeech().size() != 1) {
            // no sense to calculate fingerprint for this article, yet (maybe on later stage)
            return null;
        }

        PartOfSpeech partOfSpeech = (PartOfSpeech) article.getPartOfSpeech().toArray()[0];

        Optional<FormTypeCombination> excludedFormTypeCombination = getExcludedFormTypeCombinationPerPartOfSpeech(partOfSpeech);

        return article.getForms()
                .stream()
                .filter(articleForm -> excludedFormTypeCombination
                        .map(combination -> !combination.equals(articleForm.getFormTypeCombination()))
                        .orElse(true))
                .map(articleForm -> new FormSnapshot(articleForm.getRepresentation(), articleForm.getFormTypeCombination()))
                .distinct()
                .sorted(new SnapshotComparator())
                .map(snapshot -> String.format("[%s]:%s", snapshot.formTypeCombination.getEkiRepresentation(), snapshot.representation.getRepresentation()))
                .collect(Collectors.joining(";"));
    }

    private Optional<FormTypeCombination> getExcludedFormTypeCombinationPerPartOfSpeech(PartOfSpeech partOfSpeech) {
        if (partOfSpeech.isName()) {
            return formTypeCombinationRepository.findByEkiRepresentation("SgN");
        } else {
            if (partOfSpeech.isVerb()) {
                return formTypeCombinationRepository.findByEkiRepresentation("Sup");
            }
        }
        return Optional.empty();
    }

    @Getter
    @EqualsAndHashCode
    class FormSnapshot {
        private final Representation representation;
        private final FormTypeCombination formTypeCombination;

        public FormSnapshot(Representation representation, FormTypeCombination formTypeCombination) {
            this.representation = representation;
            this.formTypeCombination = formTypeCombination;
        }
    }

    class SnapshotComparator implements Comparator<FormSnapshot> {
        @Override
        public int compare(FormSnapshot o1, FormSnapshot o2) {
            if (o1.representation.getId().equals(o2.representation.getId())) {
                return Long.compare(o1.formTypeCombination.getId(), o2.formTypeCombination.getId());
            }
            return Long.compare(o1.representation.getId(), o2.representation.getId());
        }
    }
}
