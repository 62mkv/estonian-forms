package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "LEXEME_FORMS")
@NoArgsConstructor
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LEXEME_FORMS_SEQ")
    @SequenceGenerator(name = "LEXEME_FORMS_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    Long id;

    String wikidataId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "representation_id")
    Representation representation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lexeme_id")
    Lexeme lexeme;

    @ManyToOne
    @JoinColumn(name = "form_type_combination_id")
    FormTypeCombination formTypeCombination;

    String declinationTypes;

    @Override
    public String toString() {
        return "Form{" +
                "id=" + id +
                ", wikidataId='" + wikidataId + '\'' +
                ", representation=" + representation +
                ", lexeme=" + lexeme +
                ", formTypeCombination=" + formTypeCombination +
                ", declinationTypes='" + declinationTypes + '\'' +
                '}';
    }
}
