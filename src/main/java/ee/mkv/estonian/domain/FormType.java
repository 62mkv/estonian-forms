package ee.mkv.estonian.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity(name = "form_types")
public class FormType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String ekiCode;

    String wikidataCode;
}
