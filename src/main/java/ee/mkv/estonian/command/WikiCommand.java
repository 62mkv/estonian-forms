package ee.mkv.estonian.command;


import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.service.WikiUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "wikidata")
@Slf4j
public class WikiCommand implements Runnable {

    private final WikiUploadService wikiUploadService;
    private final LexemeRepository lexemeRepository;

    public WikiCommand(WikiUploadService wikiUploadService, LexemeRepository lexemeRepository) {
        this.wikiUploadService = wikiUploadService;
        this.lexemeRepository = lexemeRepository;
    }

    @Override
    public void run() {
        for (Lexeme lexeme : lexemeRepository.findAllByWikidataIdNull()) {
            wikiUploadService.uploadLexeme(lexeme);
        }
    }
}
