package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
public class FileLoadService {

    private static final String WORD_FORMS = "fmsynth.csv";
    private static final String ARTICLES = "basic-form.csv";
    private static final String ARTICLES_PART_OF_SPEECH = "parts_of_speech.csv";

    private final RepresentationsRepository representationsRepository;
    private final FormTypeRepository formTypeRepository;
    private final ArticleRepository articleRepository;
    private final FormRepository formRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;

    public FileLoadService(RepresentationsRepository representationsRepository, FormTypeRepository formTypeRepository, ArticleRepository articleRepository, FormRepository formRepository, PartOfSpeechRepository partOfSpeechRepository) {
        this.representationsRepository = representationsRepository;
        this.formTypeRepository = formTypeRepository;
        this.articleRepository = articleRepository;
        this.formRepository = formRepository;
        this.partOfSpeechRepository = partOfSpeechRepository;
    }

    public void loadFilesFromPath(String dirPath) {

    }

    public void dummyInitialize() {
    }
}
