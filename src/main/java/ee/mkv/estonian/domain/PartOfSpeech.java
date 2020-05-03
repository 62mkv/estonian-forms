package ee.mkv.estonian.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity(name = "PARTS_OF_SPEECH")
public class PartOfSpeech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String ekiRepresentation;

    String evsRepresentation;

    String wikidataCode;
}
