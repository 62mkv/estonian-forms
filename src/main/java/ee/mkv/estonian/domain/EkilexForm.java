package ee.mkv.estonian.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "EKILEX_FORMS")
@NoArgsConstructor
public class EkilexForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "paradigm_id")
    EkilexParadigm ekilexParadigm;

    @ManyToOne
    @JoinColumn(name = "form_type_combination_id")
    FormTypeCombination formTypeCombination;

    @ManyToOne
    @JoinColumn(name = "word_representation_id")
    Representation representation;
}
