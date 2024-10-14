package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.EkilexLexeme;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.repository.EkilexLexemeRepository;
import ee.mkv.estonian.repository.EkilexWordRepository;
import ee.mkv.estonian.repository.PartOfSpeechRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class EkilexPartOfSpeechService {

    private final EkilexLexemeRepository ekilexLexemeRepository;
    private final EkilexWordRepository ekilexWordRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;

    public void manuallyAssignPartOfSpeechToEkilexWord(Long wordId, String partOfSpeechArgument) {
        final List<EkilexLexeme> ekilexLexemes = Lists.newArrayList(ekilexLexemeRepository.findAllByWordId(wordId).iterator());
        if (ekilexLexemes.size() > 1) {
            log.warn("More than 1 EkilexLexeme mapping for given word id {}", wordId);
            return;
        }
        var partOfSpeech = partOfSpeechRepository.findByPartOfSpeech(partOfSpeechArgument)
                .orElseThrow(() -> new RuntimeException("No part of speech found: " + partOfSpeechArgument));

        if (!ekilexLexemes.isEmpty()) {
            var ekilexLexeme = ekilexLexemes.get(0);
            if (!ekilexLexeme.getPos().isEmpty()) {
                log.warn("There're already parts of speech set for this word: {}", ekilexLexeme.getPos());
                return;
            }

            ekilexLexeme.setPos(Set.of(partOfSpeech));
            ekilexLexemeRepository.save(ekilexLexeme);
            log.info("Added POS to existing EkilexLexeme [id {}]", ekilexLexeme.getId());
            return;
        }

        createNewEkilexLexemeWithPos(wordId, partOfSpeech);
    }

    private void createNewEkilexLexemeWithPos(Long wordId, PartOfSpeech partOfSpeech) {
        var newEkilexLexeme = new EkilexLexeme();
        newEkilexLexeme.setPos(Set.of(partOfSpeech));
        newEkilexLexeme.setWord(ekilexWordRepository.findById(wordId).orElseThrow(() -> new RuntimeException("No EkilexWord found with id " + wordId)));
        ekilexLexemeRepository.save(newEkilexLexeme);
        log.info("Saved EkilexLexeme with id {}", newEkilexLexeme.getId());
    }

}
