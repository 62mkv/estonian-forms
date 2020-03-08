package ee.mkv.estonian.service;

import com.opencsv.CSVReader;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.repository.RepresentationsRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileLoadService {

    private static final String WORD_FORMS = "fmsynth.csv";
    private static final String ARTICLES = "basic-form.csv";
    private static final String ARTICLES_PART_OF_SPEECH = "parts_of_speech.csv";

    private final RepresentationsRepository representationsRepository;

    public FileLoadService(RepresentationsRepository representationsRepository) {
        this.representationsRepository = representationsRepository;
    }

    public void loadFromPath(String path) throws IOException {
        Path filePath = Paths.get(path, WORD_FORMS);
        List<String[]> readList;
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                readList = csvReader.readAll();
            }
        }

        for (String[] record : readList) {
            String representationForm = record[6];
            Representation representation = new Representation(representationForm);
            representationsRepository.save(representation);
        }
    }

}
