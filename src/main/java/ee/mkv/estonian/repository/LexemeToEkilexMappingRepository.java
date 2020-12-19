package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.LexemeToEkiLexMapping;
import ee.mkv.estonian.domain.PartOfSpeech;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LexemeToEkilexMappingRepository extends CrudRepository<LexemeToEkiLexMapping, Long> {

    Optional<LexemeToEkiLexMapping> findByEkilexWordAndPartOfSpeech(EkilexWord ekilexWord, PartOfSpeech partOfSpeech);

    Optional<EkilexWord> findByLexemeAndPartOfSpeech(Lexeme lexeme, PartOfSpeech partOfSpeech);
}
