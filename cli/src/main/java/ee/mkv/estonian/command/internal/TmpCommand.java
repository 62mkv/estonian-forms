package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.ekilex.EkilexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;

@Component
@CommandLine.Command(name = "tmp-command")
@RequiredArgsConstructor
@Slf4j
public class TmpCommand extends HelpAwarePicocliCommand {

    private final EkilexService ekilexService;

    @CommandLine.Option(names = {"-ids", "--word-ids"}, split = ",", description = "Comma-separated list of word IDs")
    private List<Long> wordIds;

    @Override
    public void run() {
        log.info("Running TmpCommand with word IDs: {}", wordIds);
        var words = ekilexService.findEkilexWordsByIds(wordIds);
        log.info("Found {} words", words.size());
        words.forEach(word -> log.info("Word id: {}, parts of speech: {}", word.getId(), word.getPartsOfSpeech()));
    }

}
