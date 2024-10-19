package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "FORM_TYPE_COMBINATIONS")
@NoArgsConstructor
public class FormTypeCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String ekiRepresentation;

    @ManyToMany
    @JoinTable(name = "forms_form_types")
    @ToString.Exclude
    Set<FormType> formTypes = new HashSet<>();

    @Override
    public String toString() {
        return "FormTypeCombination{" +
                "id=" + id +
                ", ekiRepresentation='" + ekiRepresentation + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FormTypeCombination that = (FormTypeCombination) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
