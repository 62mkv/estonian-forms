package ee.mkv.estonian.command.ekilex;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.ekilex.EkiLexRetrievalService;
import ee.mkv.estonian.repository.EkilexWordRepository;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import picocli.CommandLine;

import java.util.List;

@Component
@CommandLine.Command(name = "ekilex")
@Slf4j
public class EkiLexCommand implements Runnable {
    private final EkilexWordRepository ekilexWordRepository;
    private final EkiLexRetrievalService retrievalService;

    @CommandLine.Option(names = {"-i", "--id-list"})
    private String idList;

    @CommandLine.Option(names = {"-w", "--word"})
    private String word;

    @CommandLine.Option(names = {"-f", "--force"})
    private boolean force;

    public EkiLexCommand(EkiLexRetrievalService retrievalService,
                         EkilexWordRepository ekilexWordRepository) {
        this.retrievalService = retrievalService;
        this.ekilexWordRepository = ekilexWordRepository;
    }

    @Override
    public void run() {
        if (StringUtils.isBlank(idList)) {
            if (StringUtils.isBlank(word)) {
                importStartingFromLastestImported();
            } else {
                processWord();
            }
        } else {
            processIds();
        }
    }

    private void processWord() {
        log.info("Processing word {}", word);
        List<EkilexWord> words = IterableUtils.iterableToList(ekilexWordRepository.findAllByBaseFormRepresentation(word));
        if (!words.isEmpty()) {
            for (EkilexWord ekilexWord : words) {
                log.info("Retrieve by id {}", ekilexWord.getId());
                retrievalService.retrieveById(ekilexWord.getId(), force);
            }
        } else {
            log.info("Retrieve by lemma {}", word);
            retrievalService.retrieveByLemma(word, force);
        }
    }

    private void processIds() {
        String[] ids = idList.split(",");
        for (String id : ids) {
            try {
                long wordId = Long.parseLong(id);
                EkilexWord ekilexWord = retrievalService.retrieveById(wordId, force);
                log.info("Retrieved word {}:{}", ekilexWord.getId(), ekilexWord.getBaseForm().getRepresentation());
            } catch (RestClientException webException) {
                log.error("WebError: {}", webException.getMessage());
            } catch (Exception e) {
                log.error("Exception while retrieving word {}: ", id, e);
            }
        }
    }

    private void importStartingFromLastestImported() {
        long wordId = retrievalService.getLastPersistedWordId();
        while (true) {
            try {
                EkilexWord ekilexWord = retrievalService.retrieveById(wordId, force);
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
