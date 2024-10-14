package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.PartOfSpeech;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartOfSpeechRepository extends CrudRepository<PartOfSpeech, Long> {
    @Query("SELECT p from PartOfSpeech p where p.ekiCodes like CONCAT('%',:ekiCode,'%')")
    List<PartOfSpeech> findByEkiCode(String ekiCode);

    Optional<PartOfSpeech> findByPartOfSpeech(String partOfSpeech);
}
