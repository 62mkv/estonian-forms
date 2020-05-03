package ee.mkv.estonian.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(name = "ARTICLES")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    UUID uuid;

    @ManyToOne
    @JoinColumn(name = "word_representation_id")
    Representation baseForm;

    @ManyToMany
    @JoinTable(name = "articles_parts_of_speech")
    Set<PartOfSpeech> partOfSpeech;

    @OneToMany(mappedBy = "article")
    Set<Form> forms;
}
