package ee.mkv.estonian.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "form_types")
public class FormType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String ekiRepresentation;

    String wikidataCode;
}
