package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.repository.LexemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "split-words")
@RequiredArgsConstructor
@Slf4j
public class SplitCommand extends HelpAwarePicocliCommand {

    /*
    Example: ristkülikukujuline
    Splitting the part in 2 halves, starting with last 3 characters and moving on until we find a combination :
    - ristkülikukujul + ine
    - ristkülikukuju + line
    - ristkülikukuj + uline
    - ristkülikuku + juline
    - ristkülikuk + ujuline
    - ristküliku + kujuline <- FOUND omastav for ristkülik + kujuline
     */

    private final LexemeRepository lexemeRepository;

    @Override
    public ExitStatus call() throws Exception {
        /** find 10 longest unsplit words (nouns+adjectives)*/
//        List< Representation> candidates = lexemeRepository.
        log.error("Command not implemented");
        return ExitStatus.OK;
    }

}
