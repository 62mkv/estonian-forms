package ee.mkv.estonian.domain;

import lombok.Data;
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
    @JoinColumn(name = "word_representation_id")
    Representation baseForm;

    @ManyToOne
    @JoinColumn(name = "part_of_speech_id")
    PartOfSpeech partOfSpeech;

    @ManyToOne
    @JoinColumn(name = "lexeme_id")
    Lexeme lexeme;

    private Long wordId;
    private String example;
    private String inflectionTypeNr;
    private String inflectionType;
    private boolean secondary;

    @OneToMany(mappedBy = "ekilexParadigm")
    private List<EkilexForm> forms;
}
