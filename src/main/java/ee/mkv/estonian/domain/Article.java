package ee.mkv.estonian.domain;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Table(name = "ARTICLES")
public class Article {
    @Id
    UUID id;
}
