package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.service.ExtractRootPluralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "extract-rpl")
@RequiredArgsConstructor
@Slf4j
public class ExtractRootPluralCommand extends HelpAwarePicocliCommand {

    private final ExtractRootPluralService extractRootPluralService;

    @Override
    public ExitStatus call() throws Exception {
        extractRootPluralService.extractRootPlural();

        return ExitStatus.OK;
    }

}
