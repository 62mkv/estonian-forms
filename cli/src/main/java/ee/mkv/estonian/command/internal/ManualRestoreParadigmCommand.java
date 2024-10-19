package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.service.paradigm.ManualParadigmRestoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "manual-restore-paradigm")
@RequiredArgsConstructor
@Slf4j
public class ManualRestoreParadigmCommand extends HelpAwarePicocliCommand {

    private final ManualParadigmRestoreService paradigmRestoreService;

    @CommandLine.Option(names = "-w")
    private String wordToRestore;

    @Override
    public ExitStatus call() throws Exception {
        assert wordToRestore != null;

        try {
            paradigmRestoreService.restoreParadigm(this.wordToRestore);
            return ExitStatus.OK;
        } catch (Exception e) {
            log.error("Error while restoring paradigm for word {}", wordToRestore, e);
            return ExitStatus.TERMINATION;
        }
    }

}
