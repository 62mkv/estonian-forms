package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.Article;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends CrudRepository<Article,Long> {
    Optional<Article> findByUuid(UUID uuid);
}
