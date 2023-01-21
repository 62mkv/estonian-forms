package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.Representation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepresentationsRepository extends CrudRepository<Representation, Long> {
    Optional<Representation> findByRepresentation(String representation);
}
