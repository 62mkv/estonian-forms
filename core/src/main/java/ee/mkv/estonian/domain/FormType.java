package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "form_types")
public class FormType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String ekiRepresentation;

    String wikidataCode;
}
