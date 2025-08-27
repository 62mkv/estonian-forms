package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.mapping.PartOfSpeechMapper;
import ee.mkv.estonian.repository.EkilexWordRepository;
import ee.mkv.estonian.repository.PartOfSpeechRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EkilexPartOfSpeechService {

    private final EkilexWordRepository ekilexWordRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;

    public void manuallyAssignPartOfSpeechToEkilexWord(Long wordId, String partOfSpeechArgument) {
        var partOfSpeech = partOfSpeechRepository.findByPartOfSpeechName(partOfSpeechArgument)
                .orElseThrow(() -> new RuntimeException("No part of speech found: " + partOfSpeechArgument));

        assignPosToEkilexWord(wordId, partOfSpeech);
    }

    public void assignPosToEkilexWord(Long wordId, PartOfSpeech partOfSpeech) {
        var ekilexWord = ekilexWordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("No EkilexWord found with id: " + wordId));
        ekilexWord.getPartsOfSpeech().add(PartOfSpeechMapper.fromPartOfSpeech(partOfSpeech));
        ekilexWordRepository.save(ekilexWord);
        log.info("Assigned part of speech {} to EkilexWord id {}", partOfSpeech.getPartOfSpeechName(), wordId);
    }

}
