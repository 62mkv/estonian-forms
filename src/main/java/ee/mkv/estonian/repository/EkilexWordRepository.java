package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.EkilexWord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EkilexWordRepository extends CrudRepository<EkilexWord, Long> {
    Iterable<EkilexWord> findAllByBaseFormRepresentation(String representation);
}
