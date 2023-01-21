package ee.mkv.estonian.command.internal;

import ee.mkv.estonian.service.LexemeMappingCreationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
//@CommandLine.Command(name = "lexemes-from-ekilex")
@Slf4j
public class LexemeCommand implements Runnable {

    private final LexemeMappingCreationService creationService;

    public LexemeCommand(LexemeMappingCreationService creationService) {
        this.creationService = creationService;
    }

    @Override
    public void run() {
        creationService.createMissingMappings();
    }
}
