package ee.mkv.estonian.domain;

import lombok.Data;

import javax.persistence.*;

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
