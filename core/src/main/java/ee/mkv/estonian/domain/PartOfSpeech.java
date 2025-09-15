package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "PARTS_OF_SPEECH")
public class PartOfSpeech {

    private static final Set<String> NAMES_EKI_CODES = new HashSet<>();
    private static final Set<String> VERB_EKI_CODES = new HashSet<>();

    static {
        NAMES_EKI_CODES.add("AH"); // Adjective
        NAMES_EKI_CODES.add("GS"); // Noun
        NAMES_EKI_CODES.add("ON"); // Numeral
        NAMES_EKI_CODES.add("P"); // Pronoun
        VERB_EKI_CODES.add("XV"); // Verb
    }

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
    @Column(name = "part_of_speech")
    String partOfSpeechName;

    String wikidataCode;

    public boolean isVerb() {
        return VERB_EKI_CODES.contains(this.getEkiCodes());
    }

    public boolean isName() {
        return NAMES_EKI_CODES.contains(this.getEkiCodes());
    }

    public boolean isAdjective() {
        return "AH".equals(this.getEkiCodes());
    }

    public boolean isNoun() {
        return "GS".equals(this.getEkiCodes());
    }

    public boolean isAdverb() {
        return "D".equals(this.getEkiCodes());
    }

    @Override
    public String toString() {
        return partOfSpeechName;
    }

}
