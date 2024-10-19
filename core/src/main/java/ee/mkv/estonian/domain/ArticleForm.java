package ee.mkv.estonian.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "WORD_FORMS")
@NoArgsConstructor
public class ArticleForm {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @EqualsAndHashCode.Exclude
    Article article;

    @ManyToOne
    @JoinColumn(name = "form_type_combination_id")
    @EqualsAndHashCode.Exclude
    FormTypeCombination formTypeCombination;

    @ManyToOne
    @JoinColumn(name = "word_representation_id")
    Representation representation;

    @ManyToMany
    @JoinTable
    @EqualsAndHashCode.Exclude
    Set<PartOfSpeech> partOfSpeechEntities = new HashSet<>();

    Integer declinationType;

    Integer stemLength;
}
