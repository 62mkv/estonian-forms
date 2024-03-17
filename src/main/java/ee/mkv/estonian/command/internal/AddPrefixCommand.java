package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.service.lexeme.ImmutableLexemeAdderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@CommandLine.Command(name = "add-prefix")
@Component
@RequiredArgsConstructor
@Slf4j
public class AddPrefixCommand extends HelpAwarePicocliCommand {

    private final ImmutableLexemeAdderService immutableLexemeAdderService;

    @CommandLine.Option(names = "-w")
    private String word;

    @Override
    public ExitStatus call() throws Exception {
        try {
            immutableLexemeAdderService.addImmutableLexeme(word, InternalPartOfSpeech.PREFIX);
        } catch (Exception e) {
            log.error("Error while adding prefix", e);
            return ExitStatus.TERMINATION;
        }
        return ExitStatus.OK;
    }
}
