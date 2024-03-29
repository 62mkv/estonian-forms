package ee.mkv.estonian.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.LoginFailedException;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

@Configuration
@ConditionalOnProperty("wikidata.site")
@EnableConfigurationProperties(WikidataProperties.class)
@ComponentScan(basePackages = {"ee.mkv.estonian.command.wiki", "ee.mkv.estonian.wikidata"})
@Slf4j
public class WikidataConfiguration {

    BasicApiConnection connection;
    String siteIri;

    public WikidataConfiguration(WikidataProperties properties) throws LoginFailedException {
        log.info("Connecting to {} Wikidata as {}", properties.getSite(), properties.getUsername());
        this.connection = new BasicApiConnection(properties.getSite().getUrl());
        this.connection.login(properties.getUsername(), properties.getPassword());
        this.siteIri = properties.getSite().getIri();
    }

    @Bean
    public WikibaseDataFetcher fetcher() {
        return new WikibaseDataFetcher(connection, siteIri);
    }

    @Bean
    public WikibaseDataEditor editor() {
        WikibaseDataEditor wikibaseDataEditor = new WikibaseDataEditor(connection, siteIri);
        wikibaseDataEditor.setEditAsBot(true);
        return wikibaseDataEditor;
    }
}
