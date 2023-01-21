package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.model.PartOfSpeechEnum;
import ee.mkv.estonian.repository.EkilexFormRepository;
import ee.mkv.estonian.repository.EkilexParadigmRepository;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.function.Predicate;

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
    public void testNounSingleParadigm() {
        List<Lexeme> lexemeSet = lexemeFromEkiLexService.buildLexemesFromEkiLexParadigms("ema", PartOfSpeechEnum.NOUN);
        assertThat(lexemeSet).size().isEqualTo(1);
        Lexeme lexeme = lexemeSet.toArray(new Lexeme[]{})[0];
        assertThat(lexeme.getForms()).size().isEqualTo(3);
        assertThat(lexeme.getForms()).allMatch(getFormDeclinationTypePredicate("22"));
    }

    @Test
    public void testNounDoubleParadigm() {
        List<Lexeme> lexemeSet = lexemeFromEkiLexService.buildLexemesFromEkiLexParadigms("koer", PartOfSpeechEnum.NOUN);
        assertThat(lexemeSet).size().isEqualTo(1);
        Lexeme lexeme = lexemeSet.toArray(new Lexeme[]{})[0];
        assertThat(lexeme.getForms()).size().isEqualTo(4);
        assertThat(lexeme.getForms()).areAtLeast(2, new Condition<>(getFormDeclinationTypePredicate("22,23"), " has all necessary declinations"));
        assertThat(lexeme.getForms()).areAtLeastOne(new Condition<>(getFormDeclinationTypePredicate("22"), " has declination 22"));
        assertThat(lexeme.getForms()).areAtLeastOne(new Condition<>(getFormDeclinationTypePredicate("23"), " has declination 23"));
    }

    @Test
    public void testNounHomonyms() {
        List<Lexeme> lexemeSet = lexemeFromEkiLexService.buildLexemesFromEkiLexParadigms("minema", PartOfSpeechEnum.NOUN);
        assertThat(lexemeSet).size().isEqualTo(2);
        assertThat(lexemeSet).allMatch(lexeme -> lexeme.getForms().size() == 2);

    }

    private Predicate<Form> getFormDeclinationTypePredicate(String declinationType) {
        return f -> declinationType.equals(f.getDeclinationTypes());
    }

}