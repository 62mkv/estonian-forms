package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.domain.Representation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LexemeRepository extends CrudRepository<Lexeme, Long> {
    List<Lexeme> findByLemmaAndPartOfSpeech(Representation lemma, PartOfSpeech partOfSpeech);

    Iterable<? extends Lexeme> findAllByWikidataIdNull();
}
