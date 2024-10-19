package ee.mkv.estonian.command.internal;

import ee.mkv.estonian.service.LexemeMappingCreationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

@Component
@CommandLine.Command(name = "lexemes-from-ekilex")
@Slf4j
public class LexemeFromEkilexCommand implements Runnable {

    private final LexemeMappingCreationService creationService;
    @CommandLine.Option(names = "-i", description = "db id of an ekilex word")
    private Long wordId;
    @CommandLine.Option(names = "-w", description = "ekilex word to convert to lexeme (base representation)", split = ",")
    private List<String> words = new ArrayList<>();

    public LexemeFromEkilexCommand(LexemeMappingCreationService creationService) {
        this.creationService = creationService;
    }

    @Override
    public void run() {
        if (wordId == null) {
            if (words.isEmpty()) {
                creationService.createMissingMappings();
            } else {
                processWords();
            }
        } else {
            creationService.createMissingMapping(wordId);
        }
    }

    private void processWords() {
        for (String word : words) {
            creationService.createMissingMapping(word);
        }
    }
}
