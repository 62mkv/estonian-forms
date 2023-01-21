package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.domain.LexemeToEkiLexMapping;
import ee.mkv.estonian.repository.EkilexWordRepository;
import ee.mkv.estonian.repository.LexemeToEkilexMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LexemeMappingCreationService {
    private final LexemeToEkilexMappingRepository mappingRepository;
    private final EkilexWordRepository wordRepository;
    private final LexemePersistingService persistingService;
    private final LexemeFromEkiLexService fromEkiLexService;

    public LexemeMappingCreationService(LexemeToEkilexMappingRepository mappingRepository, EkilexWordRepository wordRepository, LexemePersistingService persistingService, LexemeFromEkiLexService fromEkiLexService) {
        this.mappingRepository = mappingRepository;
        this.wordRepository = wordRepository;
        this.persistingService = persistingService;
        this.fromEkiLexService = fromEkiLexService;
    }

    public void createMissingMappings() {
        for (EkilexWord word : wordRepository.findAll()) {
            final Long wordId = word.getId();
            if (mappingRepository.existsByEkilexWordId(wordId)) {
                log.debug("Skipping word {}, mapping for it exists already", wordId);
            } else {
                log.info("Creating mappings for word id: {}", wordId);
                for (LexemeToEkiLexMapping mapping : fromEkiLexService.buildLexemesFromEkiLexWord(wordId)) {
                    persistingService.save(mapping);
                    log.info("Persisted mapping for word id: {}, mapping: {}", wordId, mapping);
                }
            }
        }
    }
}
