package ee.mkv.estonian.integration;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.service.FileLoadService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class SimpleTest {

    public static final java.util.UUID ARTICLE_UUID = java.util.UUID.randomUUID();

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    FormRepository formRepository;

    @Autowired
    FormTypeRepository formTypeRepository;

    @Autowired
    PartOfSpeechRepository partOfSpeechRepository;

    @Autowired
    RepresentationsRepository representationsRepository;

    @Autowired
    FormTypeCombinationRepository formTypeCombinationRepository;

    @Autowired
    FileLoadService fileLoadService;

    @Before
    public void setUp() {
        Representation representation = new Representation();
        representation.setRepresentation("ema");

        representationsRepository.save(representation);

        PartOfSpeech noun = partOfSpeechRepository.findByPartOfSpeech("Noun").get();
        FormType singular = formTypeRepository.findByEkiRepresentation("Sg");
        FormType nominative = formTypeRepository.findByEkiRepresentation("N");

        FormTypeCombination formTypeCombination = new FormTypeCombination();
        formTypeCombination.setEkiRepresentation("SgN");
        formTypeCombination.getFormTypes().add(singular);
        formTypeCombination.getFormTypes().add(nominative);
        formTypeCombinationRepository.save(formTypeCombination);

        Article myArticle = new Article();
        myArticle.setUuid(ARTICLE_UUID);
        myArticle.setBaseForm(representation);
        myArticle.setPartOfSpeech(Collections.singleton(noun));
        myArticle.getDeclinationTypes().add(1);
        myArticle.getDeclinationTypes().add(3);
        articleRepository.save(myArticle);
    }

    @Test
    public void simpleDataTest() {
        Article article = articleRepository.findByUuid(ARTICLE_UUID).get();
        PartOfSpeech noun = partOfSpeechRepository.findByPartOfSpeech("Noun").get();

        assertThat(article.getPartOfSpeech()).contains(noun);
        assertThat(article.getDeclinationTypes()).contains(1, 3);
    }

    @Test
    public void formAddedTest() {
        fileLoadService.createAndSaveForm(ARTICLE_UUID, "S", 1, 1, 2, "SgN", "ema", 3);
        fileLoadService.createAndSaveForm(ARTICLE_UUID, "S", 1, 1, 2, "SgN", "emai", 3);

        // this one should be rejected, as it's part of speech is not one of defined per article
        fileLoadService.createAndSaveForm(ARTICLE_UUID, "A", 1, 1, 2, "SgN", "emab", 3);

        Iterable<Form> formsCreated = formRepository.findAll();
        assertThat(formsCreated).hasSize(2);
    }
}
