package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.EkilexLexeme;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EkilexLexemeRepository extends CrudRepository<EkilexLexeme, Long> {
    Iterable<EkilexLexeme> findAllByWordId(Long wordId);
}
