package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.domain.Representation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LexemeRepository extends CrudRepository<Lexeme, Long> {
    List<Lexeme> findByLemmaAndPartOfSpeech(Representation lemma, PartOfSpeech partOfSpeech);

    List<Lexeme> findByLemma(Representation lemma);

    Iterable<Lexeme> findAllByWikidataIdNull();

    Iterable<Lexeme> findByLemmaRepresentationIn(Collection<String> lemmas);

    @Query(nativeQuery = true, value = "SELECT l.* from lexemes " +
            "JOIN representation r on r.id = l.")
    Iterable<Lexeme> findNextUnsplitCandidates();
}
