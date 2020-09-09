package ee.mkv.estonian.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "LEXEMES")
@NoArgsConstructor
public class Lexeme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String wikidataId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "representation_id")
    Representation lemma;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "part_of_speech_id")
    PartOfSpeech partOfSpeech;

    @OneToMany(mappedBy = "lexeme")
    Set<Article> articles;

    @OneToMany(mappedBy = "lexeme")
    Set<Form> forms;
}
