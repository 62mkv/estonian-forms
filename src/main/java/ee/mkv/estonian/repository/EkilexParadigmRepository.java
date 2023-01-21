package ee.mkv.estonian.repository;

import ee.mkv.estonian.domain.EkilexParadigm;
import ee.mkv.estonian.model.DiscrepancyProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

@Repository
public interface EkilexParadigmRepository extends CrudRepository<EkilexParadigm, Long> {

    Iterable<EkilexParadigm> findAllByWordId(Long id);

    boolean existsByWordId(Long id);

    @Query(value = "with base as (\n" +
            "select r.representation, ef.paradigm_id as id\n" +
            "from ekilex_forms ef  \n" +
            "join representations r on ef.word_representation_id = r.id\n" +
            "where ef.form_type_combination_id  = 16 and r.representation <> '-'\n" +
            "),\n" +
            " declined as (\n" +
            "select r.representation, ef.paradigm_id as id\n" +
            "from ekilex_forms ef\n" +
            "join representations r on ef.word_representation_id = r.id\n" +
            "where ef.form_type_combination_id  = 24 and r.representation <> '-'\n" +
            "),\n" +
            " discrepancies as (\n" +
            "select base.id, nominal.representation as nominal, base.representation as base, declined.representation as inflected, \n" +
            "       base.representation || 'ks' as suffixed\n" +
            "from base \n" +
            "join ekilex_forms ef on ef.paradigm_id = base.id and ef.form_type_combination_id = 1\n" +
            "join representations nominal on nominal.id = ef.word_representation_id \n" +
            "join declined on base.id = declined.id\n" +
            "where base.id not in (25944, 34496, 50720, 113435, 80638, 80879, 95249)\n" +
            "and base.representation||'ks' <> declined.representation\n" +
            "and not exists (select 1 from base b2 where b2.representation||'ks' = declined.representation and b2.id = base.id)\n" +
            "), rootplural as (\n" +
            "select ef.paradigm_id  as id from ekilex_forms ef where ef.form_type_combination_id = 29\n" +
            ")\n" +
            "select ep.*, d.inflected from discrepancies d\n" +
            "join ekilex_paradigms ep on d.id = ep.id\n" +
            "where not exists (select 1 from rootplural where id = d.id)\n" +
            "limit 10", nativeQuery = true)
    Streamable<DiscrepancyProjection> findNextCandidatesForRootPlural();
}
