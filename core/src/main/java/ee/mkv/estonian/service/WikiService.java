package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.wikidata.WikiUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty("wikidata.site")
public class WikiService {
    private final WikiUploadService wikiUploadService;
    private final LexemeRepository lexemeRepository;

    public void runWikiUpload() {
        for (Lexeme lexeme : lexemeRepository.findAllByWikidataIdNull()) {
            wikiUploadService.uploadLexeme(lexeme);
        }
    }

}
