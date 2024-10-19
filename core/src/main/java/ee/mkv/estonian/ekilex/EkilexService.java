package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.repository.EkilexWordRepository;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EkilexService {

    private final EkilexWordRepository ekilexWordRepository;
    private final EkiLexRetrievalService retrievalService;

    public void runEkilex(String idList, List<String> words, boolean force) {
        if (StringUtils.isBlank(idList)) {
            if (words.isEmpty()) {
                importStartingFromLastestImported(force);
            } else {
                processWords(words, force);
            }
        } else {
            processIds(idList, force);
        }
    }

    private void processWords(List<String> words1, boolean force) {
        for (String word : words1) {
            processWord(word, force);
        }
    }

    private void processWord(String word, boolean force) {
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

    private void processIds(String idList, boolean force) {
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

    private void importStartingFromLastestImported(boolean force) {
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
