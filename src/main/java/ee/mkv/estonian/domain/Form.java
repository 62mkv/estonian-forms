package ee.mkv.estonian.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity(name = "FORMS")
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    Article article;

    @ManyToMany
    @JoinTable(name = "forms_form_types")
    Set<FormType> formTypes;

    @ManyToOne
    @JoinColumn(name = "word_representation_id")
    Representation representation;
}
