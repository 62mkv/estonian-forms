package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "LOAN_WORDS")
@Entity
@Data
public class LoanWord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOAN_WORDS_SEQ")
    @SequenceGenerator(name = "LOAN_WORDS_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lexeme_id")
    private Lexeme lexeme;
}
