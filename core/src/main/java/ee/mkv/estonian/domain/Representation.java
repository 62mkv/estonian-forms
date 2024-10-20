package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "REPRESENTATIONS")
@NoArgsConstructor
public class Representation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPRESENTATIONS_SEQ")
    @SequenceGenerator(name = "REPRESENTATIONS_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;
    private String representation;
}
