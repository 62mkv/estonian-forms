package ee.mkv.estonian.command.split;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.split.NewSplitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "split-words")
@RequiredArgsConstructor
public class SplitCommand extends HelpAwarePicocliCommand {

    private final NewSplitService newSplitService;

    @Override
    public ExitStatus call() throws Exception {

        newSplitService.runSplitService();

        return ExitStatus.OK;
    }

}
