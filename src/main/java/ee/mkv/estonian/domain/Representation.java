package ee.mkv.estonian.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "REPRESENTATIONS")
public class Representation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String representation;
}
