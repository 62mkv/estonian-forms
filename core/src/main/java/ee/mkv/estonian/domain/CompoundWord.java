package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Table(name = "COMPOUND_WORDS")
@Data
@Entity
public class CompoundWord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPOUND_WORDS_SEQ")
    @SequenceGenerator(name = "COMPOUND_WORDS_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    Long id;

    @ManyToOne
    @JoinColumn(name = "lexeme_id")
    Lexeme lexeme;

    @Setter(value = AccessLevel.NONE)
    @Column(name = "rule_id")
    int compoundRule;

    @Column(name = "rejected")
    boolean rejected;

    @OneToMany(mappedBy = "compoundWord", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    List<CompoundWordComponent> components;

    public void setCompoundRule(CompoundRule compoundRule) {
        this.compoundRule = compoundRule.getId();
    }

}
