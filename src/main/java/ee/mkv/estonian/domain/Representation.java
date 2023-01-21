package ee.mkv.estonian.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "REPRESENTATIONS")
@NoArgsConstructor
public class Representation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String representation;
}
