package ee.mkv.estonian.command.internal;

import ee.mkv.estonian.service.LexemeMappingCreationService;
import ee.mkv.estonian.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "lexemes-from-ekilex")
@Slf4j
public class LexemeFromEkilexCommand implements Runnable {

    @CommandLine.Option(names = "-i", description = "db id of an ekilex word")
    private Long wordId;

    @CommandLine.Option(names = "-w", description = "ekilex word to convert to lexeme (base representation)")
    private String word;

    private final LexemeMappingCreationService creationService;

    public LexemeFromEkilexCommand(LexemeMappingCreationService creationService) {
        this.creationService = creationService;
    }

    @Override
    public void run() {
        if (wordId == null) {
            if (StringUtils.isEmpty(word)) {
                creationService.createMissingMappings();
            } else {
                creationService.createMissingMapping(word);
            }
        } else {
            creationService.createMissingMapping(wordId);
        }
    }
}
