package ee.mkv.estonian.integration;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SimpleTest {
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

    @Before
    public void setUp() {
        Representation representation = new Representation();
        representation.setRepresentation("ema");

        representationsRepository.save(representation);

        PartOfSpeech noun = partOfSpeechRepository.findByPartOfSpeech("Noun");

        FormType omastav = formTypeRepository.findByEkiRepresentation("G");

        Article myArticle = new Article();
        myArticle.setUuid(UUID.randomUUID());
        myArticle.setBaseForm(representation);
        myArticle.setPartOfSpeech(Collections.singleton(noun));
        articleRepository.save(myArticle);

        Form form = new Form();
        form.setArticle(myArticle);
        form.setFormTypes(Collections.singleton(omastav));
        formRepository.save(form);
    }

    @Test
    public void simpleDataTest() {
        articleRepository.findAll();
    }
}
