package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "PARTS_OF_SPEECH")
public class PartOfSpeech {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
