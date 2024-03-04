package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.domain.Representation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LexemeRepository extends CrudRepository<Lexeme, Long> {
    List<Lexeme> findByLemmaAndPartOfSpeech(Representation lemma, PartOfSpeech partOfSpeech);

    List<Lexeme> findByLemma_RepresentationAndPartOfSpeech(String representation, PartOfSpeech partOfSpeech);

    List<Lexeme> findByLemma(Representation lemma);

    Iterable<Lexeme> findAllByWikidataIdNull();

    Iterable<Lexeme> findByLemmaRepresentationIn(Collection<String> lemmas);

    @Query(nativeQuery = true,
            value = "select *\n" +
                    "from lexemes l\n" +
                    "join representations r on r.id  = l.representation_id \n" +
                    "where part_of_speech_id in (1,5) \n" +
                    "and not r.representation like '% %' \n" +
                    "and not exists(select 1 from compound_words cw where cw.lexeme_id = l.id)\n" +
                    "and not exists(select 1 from rejected_compound_candidates rcc where rcc.lexeme_id = l.id and rcc.rule_group_id = :rule_group_id)\n" +
                    "and length(representation) > 8\n" +
                    "order by length(r.representation) desc, l.id\n" +
                    "limit :limit\n")
    Iterable<Lexeme> findNextUnsplitCandidates(@Param("rule_group_id") int ruleGroupId, int limit);
}
