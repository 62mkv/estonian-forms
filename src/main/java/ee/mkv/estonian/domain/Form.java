package ee.mkv.estonian.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "LEXEME_FORMS")
@NoArgsConstructor
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
