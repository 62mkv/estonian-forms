package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "EKILEX_LEXEMES")
public class EkilexLexeme {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ekilex_lexeme_id_seq")
    @SequenceGenerator(name = "ekilex_lexeme_id_seq", sequenceName = "hibernate_sequence", allocationSize = 1)
    Long id;

    @ManyToOne
    @JoinColumn(name = "word_id")
    EkilexWord word;

    @ManyToMany
    @JoinTable(
            name = "ekilex_lexemes_pos",
            joinColumns = {@JoinColumn(name = "ekilex_lexeme_id")},
            inverseJoinColumns = {@JoinColumn(name = "part_of_speech_id")}
    )
    Set<PartOfSpeech> pos;
}
