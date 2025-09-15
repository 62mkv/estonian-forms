package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.LexemeToEkiLexMapping;
import ee.mkv.estonian.repository.EkilexWordRepository;
import ee.mkv.estonian.repository.LexemeToEkilexMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LexemeMappingCreationService {
    private final LexemeToEkilexMappingRepository mappingRepository;
    private final EkilexWordRepository wordRepository;
    private final LexemePersistingService lexemePersistingService;
    private final LexemeFromEkiLexService fromEkiLexService;
    private final UserInputProvider userInputProvider;

    @Transactional
    public void createMissingMapping(Long wordId) {
        createMappingForWord(wordId);
    }

    @Transactional
    public void createMissingMapping(String word) {
        List<EkilexWord> candidateWords = Lists.newArrayList(wordRepository.findAllByBaseFormRepresentation(word).iterator());
        if (candidateWords.isEmpty()) {
            log.error("No EkilexWord found for '{}'", word);
            return;
        }

        for (EkilexWord candidate : candidateWords) {
            log.info("Found candidate EkilexWord: {}", candidate);
            createMappingForWord(candidate.getId());
        }
    }

    public void createMissingMappings() {
        for (EkilexWord word : wordRepository.findAll()) {
            log.info("Checking EkiLex word id {} ({})", word.getId(), word.getBaseForm().getRepresentation());
            createMappingForWord(word.getId());
        }
    }

    private void createMappingForWord(Long wordId) {
        if (mappingRepository.existsByEkilexWordId(wordId)) {
            log.info("Mapping for word {} exists already", wordId);
            for (LexemeToEkiLexMapping mapping : mappingRepository.findByEkilexWordId(wordId)) {
                Lexeme newLexeme = fromEkiLexService.recoverLexemeFormsFromEkilexForms(mapping.getLexeme(), mapping.getEkilexWord());
                lexemePersistingService.save(newLexeme);
                log.info("Saved lexeme {}", newLexeme);
            }
        } else {
            log.info("Creating mappings for word id: {}", wordId);
            for (LexemeToEkiLexMapping mapping : fromEkiLexService.buildLexemesFromEkiLexWord(wordId)) {
                lexemePersistingService.save(mapping);
                log.info("Persisted mapping for word id: {}, mapping: {}", wordId, mapping);
            }
        }
    }
}
