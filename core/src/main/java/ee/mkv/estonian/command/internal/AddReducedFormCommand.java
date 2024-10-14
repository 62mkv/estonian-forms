package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.service.lexeme.LexemeFormRestorer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "add-reduced-form")
@RequiredArgsConstructor
@Slf4j
public class AddReducedFormCommand extends HelpAwarePicocliCommand {

    private final LexemeFormRestorer lexemeFormRestorer;
    @CommandLine.Option(names = "-w")
    private String wordToRestore;

    @Override
    public ExitStatus call() {
        if (wordToRestore == null) {
            log.error("Word to restore is not provided");
            return ExitStatus.TERMINATION;
        }
        log.info("Running add-reduced-form command for word {}", wordToRestore);
        try {
            lexemeFormRestorer.restoreLexemeForms(wordToRestore);
        } catch (Exception e) {
            log.error("Error while restoring paradigm for word {}", wordToRestore, e);
            return ExitStatus.TERMINATION;
        }
        return ExitStatus.OK;
    }
}
