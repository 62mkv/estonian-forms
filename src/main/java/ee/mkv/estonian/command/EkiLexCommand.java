package ee.mkv.estonian.command;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.ekilex.EkiLexRetrievalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "ekilex")
@Slf4j
public class EkiLexCommand implements Runnable {
    private final EkiLexRetrievalService retrievalService;

    @CommandLine.Option(names = {"-i", "--id-list"})
    private String idList;

    public EkiLexCommand(EkiLexRetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @Override
    public void run() {
        if (StringUtils.isBlank(idList)) {
            importStartingFromLastestImported();
        } else {
            String[] ids = idList.split(",");
            for (String id : ids) {
                try {
                    long wordId = Long.valueOf(id);
                    EkilexWord ekilexWord = retrievalService.retrieveById(wordId, false);
                    log.info("Retrieved word {}:{}", ekilexWord.getId(), ekilexWord.getBaseForm().getRepresentation());
                } catch (RestClientException webException) {
                    log.error("WebError: {}", webException.getMessage());
                } catch (Exception e) {
                    log.error("Exception while retrieving word {}: ", id, e);
                }
            }
        }
    }

    private void importStartingFromLastestImported() {
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
