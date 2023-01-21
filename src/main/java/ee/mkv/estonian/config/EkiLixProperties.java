package ee.mkv.estonian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ekilex")
@Data
public class EkiLixProperties {
    private String token;
    private String uri;
}
