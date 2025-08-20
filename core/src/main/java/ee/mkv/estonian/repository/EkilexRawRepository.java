package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.EkilexRaw;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EkilexRawRepository extends CrudRepository<EkilexRaw, Long> {
    List<EkilexRaw> findByEndpointAndKey(String endpoint, String key);
}
