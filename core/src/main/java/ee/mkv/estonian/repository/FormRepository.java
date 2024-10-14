package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FormRepository extends CrudRepository<Form, Long> {
    @Query("from Form f " +
            "where f.representation.representation in (:candidates) ")
    List<Form> findWhereRepresentationIn(@Param("candidates") Collection<String> candidates);

    List<Form> findByLexeme(Lexeme lexeme);
}
