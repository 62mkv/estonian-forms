package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.EkilexForm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EkilexFormRepository extends CrudRepository<EkilexForm, Long> {
}
