package ee.mkv.estonian.service;

import com.opencsv.CSVReader;
import ee.mkv.estonian.domain.FormType;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.repository.FormTypeRepository;
import ee.mkv.estonian.repository.RepresentationsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ee.mkv.estonian.utils.StringUtils.splitFormCode;

@Service
@Slf4j
public class FileLoadService {

    private static final String WORD_FORMS = "fmsynth.csv";
    private static final String ARTICLES = "basic-form.csv";
    private static final String ARTICLES_PART_OF_SPEECH = "parts_of_speech.csv";

    private final RepresentationsRepository representationsRepository;
    private final FormTypeRepository formTypeRepository;

    public FileLoadService(RepresentationsRepository representationsRepository, FormTypeRepository formTypeRepository) {
        this.representationsRepository = representationsRepository;
        this.formTypeRepository = formTypeRepository;
    }

    public void loadFilesFromPath(String dirPath) {

    }
}
