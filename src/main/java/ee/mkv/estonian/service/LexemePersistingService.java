package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.LexemeToEkiLexMapping;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.repository.LexemeToEkilexMappingRepository;
import org.springframework.stereotype.Service;

@Service
public class LexemePersistingService {
    private final LexemeRepository lexemeRepository;
    private final FormRepository formRepository;
    private final LexemeToEkilexMappingRepository mappingRepository;

    public LexemePersistingService(LexemeRepository lexemeRepository, FormRepository formRepository, LexemeToEkilexMappingRepository mappingRepository) {
        this.lexemeRepository = lexemeRepository;
        this.formRepository = formRepository;
        this.mappingRepository = mappingRepository;
    }

    public void save(LexemeToEkiLexMapping mapping) {
        final Lexeme lexeme = mapping.getLexeme();
        final PartOfSpeech partOfSpeech = lexeme.getPartOfSpeech();

        lexemeRepository.save(lexeme);
        for (Form form : lexeme.getForms()) {
            formRepository.save(form);
        }
        mappingRepository.save(mapping);
    }
}
