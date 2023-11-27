package ee.mkv.estonian.domain;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "LEXEMES")
@NoArgsConstructor
public class Lexeme {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String wikidataId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "representation_id")
    Representation lemma;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "part_of_speech_id")
    PartOfSpeech partOfSpeech;

    @OneToMany(mappedBy = "lexeme")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<Form> forms = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Lexeme lexeme = (Lexeme) o;
        return id != null && Objects.equals(id, lexeme.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
