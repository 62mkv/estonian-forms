package ee.mkv.estonian.command;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.ekilex.EkiLexRetrievalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "ekilex")
@Slf4j
public class EkiLexCommand implements Runnable {
    private final EkiLexRetrievalService retrievalService;

    @CommandLine.Option(names = {"-l", "--lemma-list"})
    private String lemma;
    @CommandLine.Option(names = {"-p", "--partOfSpeech"})
    private String partOfSpeech;

    public EkiLexCommand(EkiLexRetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @Override
    public void run() {
        long wordId = retrievalService.getLastPersistedWordId();
        while (true) {
            try {
                EkilexWord ekilexWord = retrievalService.retrieveById(wordId, false);
                log.info("Retrieved word {}:{}", ekilexWord.getId(), ekilexWord.getBaseForm().getRepresentation());
                wordId++;
            } catch (RestClientException webException) {
                log.error("WebError: {}", webException.getMessage());
            } catch (Exception e) {
                log.error("Exception while retrieving word {}: ", wordId, e);
                wordId++;
            }
        }
    }
}
