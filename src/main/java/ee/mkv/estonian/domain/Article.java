package ee.mkv.estonian.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "ARTICLES")
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    UUID uuid;

    @ManyToOne
    @JoinColumn(name = "word_representation_id")
    Representation baseForm;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "articles_parts_of_speech")
    Set<PartOfSpeech> partOfSpeech;

    @OneToMany(mappedBy = "article")
    Set<Form> forms;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ARTICLE_DECLINATION_TYPES", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "declination_type")
    Set<Integer> declinationTypes = new HashSet<>();
}
