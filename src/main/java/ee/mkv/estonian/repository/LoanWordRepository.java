package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.LoanWord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanWordRepository extends CrudRepository<LoanWord, Long> {

}
