package ee.mkv.estonian.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "FORM_TYPE_COMBINATIONS")
@NoArgsConstructor
public class FormTypeCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String ekiRepresentation;

    @ManyToMany
    @JoinTable(name = "forms_form_types")
    Set<FormType> formTypes = new HashSet<>();
}
