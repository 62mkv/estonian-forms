package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.FormTypeCombination;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormTypeCombinationRepository extends CrudRepository<FormTypeCombination, Long> {
    Optional<FormTypeCombination> findByEkiRepresentation(String ekiRepresentation);
}
