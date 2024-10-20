package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "LEXEME_EKILEX_MAPPING")
public class LexemeToEkiLexMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LEXEME_TO_EKILEX_MAPPINGS_SEQ")
    @SequenceGenerator(name = "LEXEME_TO_EKILEX_MAPPINGS_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "lexeme_id")
    private Lexeme lexeme;

    @ManyToOne
    @JoinColumn(name = "part_of_speech_id")
    private PartOfSpeech partOfSpeech;

    @ManyToOne
    @JoinColumn(name = "ekilex_word_id")
    private EkilexWord ekilexWord;

}
