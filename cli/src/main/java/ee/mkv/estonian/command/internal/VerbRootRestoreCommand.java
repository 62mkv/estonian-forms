package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.service.VerbRootRestoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "verb-root-restore")
@RequiredArgsConstructor
@Slf4j
public class VerbRootRestoreCommand extends HelpAwarePicocliCommand {

    private final VerbRootRestoreService verbRootRestoreService;

    @CommandLine.Option(names = {"-w", "--word"}, description = "Word to restore verb roots for", required = true)
    private String word;

    @Override
    public ExitStatus call() throws Exception {
        try {
            verbRootRestoreService.restoreVerbRoots(word);
            return ExitStatus.OK;
        } catch (Exception e) {
            log.error("Error while restoring verb roots", e);
            return ExitStatus.TERMINATION;
        }
    }
}
