package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.ekilex.dto.DetailsDto;
import ee.mkv.estonian.ekilex.dto.SearchResultDto;
import ee.mkv.estonian.ekilex.dto.WordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EkiLexClient {

    private final static long REQUEST_INTERVAL = 3 * 1000L;

    private final RestTemplate restTemplate;
    private Instant lastRequest = Instant.now();

    public EkiLexClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Set<Long> findWords(String word) {
        return pause(() -> restTemplate.getForObject("/api/word/search/{word}", SearchResultDto.class, word)
                .getWords()
                .stream()
                .filter(wordDto -> "est".equalsIgnoreCase(wordDto.getLang()))
                .map(WordDto::getWordId)
                .collect(Collectors.toSet()));
    }

    public DetailsDto getDetails(Long id) {
        return pause(() -> restTemplate.getForObject("/api/word/details/{id}/sss/", DetailsDto.class, id));
    }

    private synchronized <T> T pause(Supplier<T> action) {
        final long spentAfterLastRequest = Duration.between(lastRequest, Instant.now()).toMillis();
        long needToWait = REQUEST_INTERVAL - spentAfterLastRequest;
        if (needToWait > 0) {
            try {
                Thread.sleep(needToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        T result = action.get();
        lastRequest = Instant.now();
        return result;
    }
}
