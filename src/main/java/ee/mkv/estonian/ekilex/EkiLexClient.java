package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.ekilex.dto.DetailsDto;
import ee.mkv.estonian.ekilex.dto.SearchResultDto;
import ee.mkv.estonian.ekilex.dto.WordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EkiLexClient {
    private static final Set<String> supportedPartsOfSpeech = new HashSet<>();

    static {
        supportedPartsOfSpeech.add("Noun");
    }

    private final RestTemplate restTemplate;

    public EkiLexClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Set<Long> findWords(String word) {

        SearchResultDto result = restTemplate.getForObject("/api/word/search/{word}", SearchResultDto.class, word);
        return result.getWords().stream()
                .filter(wordDto -> "est".equalsIgnoreCase(wordDto.getLang()))
                .map(WordDto::getWordId)
                .collect(Collectors.toSet());
    }

    public DetailsDto getDetails(Long id) {
        return restTemplate.getForObject("/api/word/details/{id}/sss/", DetailsDto.class, id);
    }
}
