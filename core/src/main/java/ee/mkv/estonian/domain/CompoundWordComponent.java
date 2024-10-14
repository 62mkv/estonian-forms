package ee.mkv.estonian.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "COMPOUND_WORD_COMPONENTS")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class CompoundWordComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    @JoinColumn(name = "compound_word_id")
    @ToString.Exclude
    CompoundWord compoundWord;

    /**
     * The index of the component in the compound word
     */
    int componentIndex;

    /**
     * The index of the character in the compound word where the component starts
     */
    int componentStartsAt;

    @ManyToOne
    @JoinColumn(name = "form_id")
    Form form;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompoundWordComponent component = (CompoundWordComponent) o;
        return id != null && Objects.equals(id, component.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
