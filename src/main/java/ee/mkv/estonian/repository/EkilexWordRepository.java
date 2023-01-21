package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.EkilexWord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EkilexWordRepository extends CrudRepository<EkilexWord, Long> {
    Iterable<EkilexWord> findAllByBaseFormRepresentation(String representation);

    @Query("select max(id) from EkilexWord")
    Optional<Long> getLastRetrievedWordId();
}
