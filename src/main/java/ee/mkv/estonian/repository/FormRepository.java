package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.ArticleForm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends CrudRepository<ArticleForm,Long>  {
}
