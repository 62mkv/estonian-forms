package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "PARTS_OF_SPEECH")
public class PartOfSpeech {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PART_OF_SPEECH_SEQ")
    @SequenceGenerator(name = "PART_OF_SPEECH_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    Long id;

    /**
     * Example: A,H, etc.
     */
    String ekiCodes;

    /**
     * Example: Numeral, Noun, etc
     */
    String partOfSpeech;

    String wikidataCode;
}
