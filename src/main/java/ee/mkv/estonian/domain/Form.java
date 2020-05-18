package ee.mkv.estonian.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "WORD_FORMS")
@NoArgsConstructor
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @EqualsAndHashCode.Exclude
    Article article;

    @ManyToMany
    @JoinTable(name = "forms_form_types")
    Set<FormType> formTypes;

    @ManyToOne
    @JoinColumn(name = "word_representation_id")
    Representation representation;
}
