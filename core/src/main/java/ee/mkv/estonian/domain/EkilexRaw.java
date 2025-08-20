package ee.mkv.estonian.domain;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.Instant;

@Entity
@Table(name = "EKILEX_RAW")
@Data
public class EkilexRaw {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EKILEX_RAW_SEQ")
    @SequenceGenerator(name = "EKILEX_RAW_SEQ", sequenceName = "hibernate_sequence", allocationSize = 1)
    Long id;

    String endpoint;

    String key;

    // The raw JSON data as a string
    @Type(JsonType.class)
    String rawData;

    Instant createdAt;
}
