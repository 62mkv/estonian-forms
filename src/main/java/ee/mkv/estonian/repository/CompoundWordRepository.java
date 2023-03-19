package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.CompoundWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompoundWordRepository extends JpaRepository<CompoundWord, Long> {
}
