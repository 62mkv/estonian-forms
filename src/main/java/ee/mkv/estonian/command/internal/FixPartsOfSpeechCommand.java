package ee.mkv.estonian.command.internal;

import ee.mkv.estonian.service.FixPartsOfSpeechService;
import org.springframework.stereotype.Component;

@Component
//@CommandLine.Command(name = "fix-pos")
public class FixPartsOfSpeechCommand implements Runnable {

    private final FixPartsOfSpeechService fixPartsOfSpeechService;

    public FixPartsOfSpeechCommand(FixPartsOfSpeechService fixPartsOfSpeechService) {
        this.fixPartsOfSpeechService = fixPartsOfSpeechService;
    }

    @Override
    public void run() {
        fixPartsOfSpeechService.fixPartsOfSpeech();
    }
}
