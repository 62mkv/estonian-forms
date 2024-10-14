package ee.mkv.estonian.service;

import com.opencsv.CSVReaderBuilder;
import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class FileLoadService {

    private static final String WORD_FORMS = "fmsynth.csv";
    private static final String ARTICLES = "basic-form.csv";
    private static final String ARTICLES_PART_OF_SPEECH = "parts_of_speech.csv";
    private static final String DECLINATION_TYPES = "declination_types.csv";

    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private final RepresentationRepository representationsRepository;
    private final FormTypeRepository formTypeRepository;
    private final ArticleRepository articleRepository;
    private final ArticleFormRepository articleFormRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;

    public FileLoadService(RepresentationRepository representationsRepository, FormTypeRepository formTypeRepository, ArticleRepository articleRepository, ArticleFormRepository articleFormRepository, PartOfSpeechRepository partOfSpeechRepository, FormTypeCombinationRepository formTypeCombinationRepository) {
        this.representationsRepository = representationsRepository;
        this.formTypeRepository = formTypeRepository;
        this.articleRepository = articleRepository;
        this.articleFormRepository = articleFormRepository;
        this.partOfSpeechRepository = partOfSpeechRepository;
        this.formTypeCombinationRepository = formTypeCombinationRepository;
    }

    public void loadFilesFromPath(String dirPath, Set<LoadMode> loadModes) throws IOException {

        if (loadModes.contains(LoadMode.LOAD_ARTICLES)) {
            loadArticles(dirPath);
        }

        if (loadModes.contains(LoadMode.LOAD_PARTS_OF_SPEECH)) {
            loadPartsOfSpeech(dirPath);
        }

        if (loadModes.contains(LoadMode.LOAD_DECLINATION_TYPES)) {
            loadDeclinationTypes(dirPath);
        }

        if (loadModes.contains(LoadMode.LOAD_FORMS)) {
            loadForms(dirPath);
        }
    }

    private void loadDeclinationTypes(String dirPath) throws IOException {
        log.info("Loading from {}", DECLINATION_TYPES);
        for (String[] record : loadFile(dirPath, DECLINATION_TYPES)) {
            UUID articleGuid = UUID.fromString(record[0]);
            try {
                Integer declinationType = Integer.parseInt(record[1]);
                setDeclinationTypePerArticle(articleGuid, declinationType);
            } catch (Exception e) {
                log.error("Exception while applying declination type for article", e);
            }

        }
    }

    private void setDeclinationTypePerArticle(UUID articleGuid, Integer declinationType) {
        articleRepository.findByUuid(articleGuid).ifPresent(article -> {
            article.getDeclinationTypes().add(declinationType);
            articleRepository.save(article);
        });
    }

    private void loadForms(String dirPath) throws IOException {
        log.info("Loading from {}", WORD_FORMS);
        for (String[] record : loadFile(dirPath, WORD_FORMS)) {
            // GUID,Part of speech,Declination type,Options count,Parallel forms count,Form code,Form representation,Stem length
            UUID articleGuid = UUID.fromString(record[0]);
            String ekiPartOfSpeech = record[1];
            Integer declinationType = Integer.parseInt(record[2]);
            Integer optionsCount = Integer.parseInt(record[3]);
            Integer parallelFormsCount = Integer.parseInt(record[4]);
            String formCode = record[5];
            String formRepresentation = record[6];
            Integer stemLength = Integer.parseInt(record[7]);
            createAndSaveForm(articleGuid, ekiPartOfSpeech, declinationType, optionsCount, parallelFormsCount, formCode, formRepresentation, stemLength);
        }
    }

    private void loadPartsOfSpeech(String dirPath) throws IOException {
        log.info("Loading from {}", ARTICLES_PART_OF_SPEECH);
        for (String[] record : loadFile(dirPath, ARTICLES_PART_OF_SPEECH)) {
            UUID articleGuid = UUID.fromString(record[0]);
            String partOfSpeech = record[1];
            updateArticlePartOfSpeech(articleGuid, createAndSavePartOfSpeech(partOfSpeech));
        }
    }

    private void loadArticles(String dirPath) throws IOException {
        log.info("Loading from {}", ARTICLES);
        for (String[] record : loadFile(dirPath, ARTICLES)) {
            String baseForm = record[0];
            UUID articleGuid = UUID.fromString(record[1]);
            createAndSaveArticle(baseForm, articleGuid);
        }
    }

    public void createAndSaveForm(UUID articleGuid, String ekiPartOfSpeech, Integer declinationType, Integer optionsCount, Integer parallelFormsCount, String formCode, String formRepresentation, Integer stemLength) {
        Article article = articleRepository.findByUuid(articleGuid).get();
        ArticleForm articleForm = new ArticleForm();
        articleForm.setArticle(article);
        boolean formFitsArticle = false;
        for (PartOfSpeech partOfSpeech : parseFormPartsOfSpeech(ekiPartOfSpeech)) {
            articleForm.getPartOfSpeechEntities().add(partOfSpeech);
            if (article.getPartOfSpeech().contains(partOfSpeech)) {
                formFitsArticle = true;
            }
        }

        if (!formFitsArticle && article.getPartOfSpeech().size() > 0) {
            log.warn("Form {}:{} does not fit article UUID {}", formRepresentation, ekiPartOfSpeech, articleGuid);
            return;
        }

        FormTypeCombination combination = createAndSaveFormTypeCombination(formCode);
        articleForm.setFormTypeCombination(combination);

        Representation representation = createAndSaveRepresentation(formRepresentation);
        articleForm.setRepresentation(representation);

        articleForm.setDeclinationType(declinationType);
        articleForm.setStemLength(stemLength);
        try {
            articleFormRepository.save(articleForm);
        } catch (Exception e) {
            log.error("Exception in creation of form {}:{}:{} {}", articleGuid, formCode, formRepresentation, e.getMessage());
        }
    }

    private Collection<PartOfSpeech> parseFormPartsOfSpeech(String ekiPartOfSpeech) {
        List<PartOfSpeech> result = new ArrayList<>();
        for (char c : ekiPartOfSpeech.toCharArray()) {
            String ekiCode = String.valueOf(c);
            for (PartOfSpeech partOfSpeech : partOfSpeechRepository.findByEkiCode(ekiCode)) {
                result.add(partOfSpeech);
            }
        }
        return result;
    }

    private FormTypeCombination createAndSaveFormTypeCombination(String formCode) {
        return formTypeCombinationRepository.findByEkiRepresentation(formCode).orElseGet(() -> {
            FormTypeCombination form = new FormTypeCombination();
            form.setFormTypes(new HashSet<>());
            for (String ekiForm : StringUtils.splitFormCode(formCode)) {
                FormType formType = formTypeRepository.findByEkiRepresentation(ekiForm);
                form.getFormTypes().add(formType);
            }

            form.setEkiRepresentation(formCode);
            return formTypeCombinationRepository.save(form);
        });
    }

    private PartOfSpeech createAndSavePartOfSpeech(String partOfSpeech) {
        return partOfSpeechRepository.findByPartOfSpeech(partOfSpeech).get();
    }

    private void updateArticlePartOfSpeech(UUID articleGuid, PartOfSpeech partOfSpeech) {
        articleRepository.findByUuid(articleGuid).ifPresent(article -> {
            article.getPartOfSpeech().add(partOfSpeech);
            articleRepository.save(article);
        });
    }

    private Article createAndSaveArticle(String baseForm, UUID articleGuid) {
        final Article article = articleRepository.findByUuid(articleGuid)
                .orElseGet(() -> {
                    Article result = new Article();
                    result.setBaseForm(createAndSaveRepresentation(baseForm));
                    result.setUuid(articleGuid);
                    return result;
                });
        return articleRepository.save(article);
    }

    private Representation createAndSaveRepresentation(String form) {
        String baseForm = StringUtils.adaptBaseForm(form);
        final Representation representation = representationsRepository.findByRepresentation(baseForm)
                .orElseGet(() -> {
                    Representation result = new Representation();
                    result.setRepresentation(baseForm);
                    return result;
                });
        return representationsRepository.save(representation);
    }

    private List<String[]> loadFile(String dirPath, String filename) throws IOException {

        return new CSVReaderBuilder(new FileReader(dirPath.concat(File.separator).concat(filename)))
                .withSkipLines(1)
                .build()
                .readAll();
    }

}
