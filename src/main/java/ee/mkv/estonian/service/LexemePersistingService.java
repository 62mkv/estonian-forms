package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.LexemeToEkiLexMapping;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.repository.LexemeToEkilexMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class LexemePersistingService {
    private final LexemeRepository lexemeRepository;
    private final FormRepository formRepository;
    private final LexemeToEkilexMappingRepository mappingRepository;

    public LexemePersistingService(LexemeRepository lexemeRepository, FormRepository formRepository, LexemeToEkilexMappingRepository mappingRepository) {
        this.lexemeRepository = lexemeRepository;
        this.formRepository = formRepository;
        this.mappingRepository = mappingRepository;
    }

    @Transactional
    public void save(LexemeToEkiLexMapping mapping) {
        final Lexeme lexeme = mapping.getLexeme();

        for (Form form : lexeme.getForms()) {
            formRepository.save(form);
        }
        lexemeRepository.save(lexeme);
        mappingRepository.save(mapping);
    }


    @Transactional
    public void save(Lexeme lexeme) {

        for (Form form : lexeme.getForms()) {
            formRepository.save(form);
            log.info("Saved form: {}", form);
        }

        lexemeRepository.save(lexeme);
    }
}
