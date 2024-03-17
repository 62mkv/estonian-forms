package ee.mkv.estonian.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Table(name = "COMPOUND_WORDS")
@Data
@Entity
public class CompoundWord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
