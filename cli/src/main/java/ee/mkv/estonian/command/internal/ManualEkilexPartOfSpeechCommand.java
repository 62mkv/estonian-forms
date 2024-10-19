package ee.mkv.estonian.command.internal;

import ee.mkv.estonian.service.EkilexPartOfSpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@CommandLine.Command(name = "manual-ekilex-pos")
@Component
@RequiredArgsConstructor
public class ManualEkilexPartOfSpeechCommand implements Runnable {

    private final EkilexPartOfSpeechService ekilexPartOfSpeechService;

    @CommandLine.Option(names = {"-w"}, description = "wordId of an Ekilex word")
    private Long wordId;

    @CommandLine.Option(names = {"-pos"}, description = "PartOfSpeech")
    private String partOfSpeech;

    @Override
    public void run() {
        ekilexPartOfSpeechService.manuallyAssignPartOfSpeechToEkilexWord(wordId, partOfSpeech);
    }
}
