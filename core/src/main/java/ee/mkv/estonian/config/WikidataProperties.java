package ee.mkv.estonian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wikidata")
@Data
public class WikidataProperties {
    private String username;
    private String password;
    private WikidataSite site;

    public enum WikidataSite {
        TEST("https://test.wikidata.org/w/api.php", "http://test.wikidata.org/entity/"),
        PROD("https://www.wikidata.org/w/api.php", "http://wikidata.org/entity/");

        private final String url;
        private final String iri;

        WikidataSite(String url, String iri) {
            this.url = url;
            this.iri = iri;
        }

        public String getUrl() {
            return url;
        }

        public String getIri() {
            return iri;
        }
    }
}
