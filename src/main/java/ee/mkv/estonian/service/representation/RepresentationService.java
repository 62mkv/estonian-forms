package ee.mkv.estonian.service.representation;


import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.repository.RepresentationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepresentationService {
    private final RepresentationRepository representationRepository;

    public Representation findOrCreate(String representation) {
        return representationRepository.findByRepresentation(representation)
                .orElseGet(() -> saveRepresentation(representation));
    }

    private Representation saveRepresentation(String representation) {
        var rep = new Representation();
        rep.setRepresentation(representation);
        return representationRepository.save(rep);
    }
}
