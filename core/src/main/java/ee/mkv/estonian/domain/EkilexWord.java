package ee.mkv.estonian.domain;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Data
@Entity
@Table(name = "EKILEX_WORDS")
public class EkilexWord {

    @Id
    @NaturalId
    Long id;

    @ManyToOne
    @JoinColumn(name = "word_representation_id")
    Representation baseForm;

}
