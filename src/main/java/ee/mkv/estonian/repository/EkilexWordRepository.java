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

    @Query(value = "SELECT ew.* from EKILEX_WORDS ew\n"
            + "where not EXISTS (SELECT 1 from LEXEME_EKILEX_MAPPING lem where lem.ekilex_word_id = ew.id)\n"
            + "and exists (SELECT 1 from EKILEX_PARADIGMS ep where ep.word_id = ew.id)\n"
            + "#pageable"
            ,
            nativeQuery = true)
    Iterable<EkilexWord> findAllWithoutLexemeButWithParadigm();
}
