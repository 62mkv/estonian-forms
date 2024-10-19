package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "LOAN_WORDS")
@Entity
@Data
public class LoanWord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lexeme_id")
    private Lexeme lexeme;
}
