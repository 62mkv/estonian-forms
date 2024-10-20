package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "EKILEX_PARADIGMS")
@NoArgsConstructor
public class EkilexParadigm {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EKILEX_PARADIGMS_SEQ")
    @SequenceGenerator(name = "EKILEX_PARADIGMS_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    Long id;

    Long ekilexId;

    @ManyToOne
    @JoinColumn(name = "word_id")
    EkilexWord word;

    private String inflectionType;

    @OneToMany(mappedBy = "ekilexParadigm")
    @EqualsAndHashCode.Exclude
    private List<EkilexForm> forms = new ArrayList<>();
}
