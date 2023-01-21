package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.model.PartOfSpeechEnum;
import ee.mkv.estonian.repository.EkilexFormRepository;
import ee.mkv.estonian.repository.EkilexParadigmRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("h2test")
@DataJpaTest
@RunWith(SpringJUnit4ClassRunner.class)
public class LexemeFromEkiLexServiceTest {

    @Autowired
    private EkilexParadigmRepository ekilexParadigmRepository;

    @Autowired
    private EkilexFormRepository ekilexFormRepository;

    private LexemeFromEkiLexService lexemeFromEkiLexService;

    @Before
    public void setUp() {
        this.lexemeFromEkiLexService = new LexemeFromEkiLexService(ekilexParadigmRepository, ekilexFormRepository);
    }

    @Test
    public void testSingleNoun() {
        Set<Lexeme> lexemeSet = lexemeFromEkiLexService.buildLexemesFromEkiLexParadigms("ema", PartOfSpeechEnum.NOUN);
        assertThat(lexemeSet).size().isEqualTo(1);
        Lexeme lexeme = lexemeSet.toArray(new Lexeme[]{})[0];
        assertThat(lexeme.getForms()).size().isEqualTo(3);
    }
}