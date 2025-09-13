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

    List<Lexeme> findAllByPartOfSpeech(PartOfSpeech partOfSpeech);

    List<Lexeme> findByLemma(Representation lemma);

    Iterable<Lexeme> findAllByWikidataIdNull();

    Iterable<Lexeme> findByLemmaRepresentationIn(Collection<String> lemmas);

    @Query(nativeQuery = true,
            value = """
                    select l.*
                    from lexemes l
                    join representations r on r.id  = l.representation_id\s
                    and not r.representation like '% %'\s
                    and not exists(select 1 from compound_words cw where cw.lexeme_id = l.id)
                    and not exists(select 1 from loan_words lw where lw.lexeme_id = l.id)
                    and length(representation) > 8
                    order by length(r.representation) desc, l.id
                    limit :limit
                    """)
    Iterable<Lexeme> findNextUnsplitCandidates(int limit);
}
