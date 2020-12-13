package ee.mkv.estonian.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "EKILEX_PARADIGMS")
@NoArgsConstructor
public class EkilexParadigm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "word_id")
    EkilexWord word;

    private String inflectionType;
    private Boolean secondary;

    @OneToMany(mappedBy = "ekilexParadigm")
    @EqualsAndHashCode.Exclude
    private List<EkilexForm> forms;
}
