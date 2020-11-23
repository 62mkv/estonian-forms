package ee.mkv.estonian.service;

import ee.mkv.estonian.dto.ParadigmDto;
import ee.mkv.estonian.dto.SearchResultDto;
import ee.mkv.estonian.dto.WordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EkiLexRetrievalService {
    private static final Set<String> supportedPartsOfSpeech = new HashSet<>();

    static {
        supportedPartsOfSpeech.add("Noun");
    }

    private final RestTemplate restTemplate;

    public EkiLexRetrievalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Set<Long> findWords(String word, String partOfSpeech) {
        if (!supportedPartsOfSpeech.contains(partOfSpeech)) {
            throw new RuntimeException(String.format("Part of speech %s is not supported for search yet"));
        }

        SearchResultDto result = restTemplate.getForObject("/api/word/search/{word}", SearchResultDto.class, word);
        return result.getWords().stream()
                .filter(wordDto -> matchesPartOfSpeech(wordDto, partOfSpeech))
                .map(WordDto::getWordId)
                .collect(Collectors.toSet());
    }

    public ParadigmDto[] getParadigmById(Long id) {
        return restTemplate.getForObject("/api/paradigm/details/{id}/", ParadigmDto[].class, id);
    }

    private boolean matchesPartOfSpeech(WordDto wordDto, String partOfSpeech) {
        if (partOfSpeech.equalsIgnoreCase("Noun") && wordDto.getWordClass().equals("noomen")) {
            return true;
        }

        return false;
    }
}
