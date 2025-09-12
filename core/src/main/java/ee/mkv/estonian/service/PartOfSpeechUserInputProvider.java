package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.PartOfSpeechRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PartOfSpeechUserInputProvider {

    private final UserInputProvider userInputProvider;
    private final PartOfSpeechRepository partOfSpeechRepository;

    public List<PartOfSpeech> getPartOfSpeech() {
        var options = InternalPartOfSpeech.values();
        for (int i = 0; i < options.length; i++) {
            log.info("{}: {}", i, options[i]);
        }
        var choice = userInputProvider.getUserChoice();
        return partOfSpeechRepository.findByEkiCode(options[choice].getEkiCodes());
    }
}
