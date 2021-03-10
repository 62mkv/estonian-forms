package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.EkilexParadigm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EkilexParadigmRepository extends CrudRepository<EkilexParadigm, Long> {

    Iterable<EkilexParadigm> findAllByWordId(Long id);

    boolean existsByWordId(Long id);
}
