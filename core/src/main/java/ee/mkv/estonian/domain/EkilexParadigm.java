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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
