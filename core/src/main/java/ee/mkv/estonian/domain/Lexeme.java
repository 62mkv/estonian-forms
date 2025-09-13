package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LEXEMES_SEQ")
    @SequenceGenerator(name = "LEXEMES_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
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

    public boolean isName() {
        return partOfSpeech.isName();
    }

    public boolean isAdjective() {
        return partOfSpeech.isAdjective();
    }

    public boolean isVerb() {
        return partOfSpeech.isVerb();
    }
}
