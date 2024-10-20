package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "form_types")
public class FormType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FORM_TYPES_SEQ")
    @SequenceGenerator(name = "FORM_TYPES_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    Long id;

    String ekiRepresentation;

    String wikidataCode;
}
