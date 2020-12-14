package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.EkilexFormRepository;
import ee.mkv.estonian.repository.EkilexLexemeRepository;
import ee.mkv.estonian.repository.EkilexParadigmRepository;
import ee.mkv.estonian.repository.EkilexWordRepository;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
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

    @Autowired
    private EkilexLexemeRepository ekilexLexemeRepository;

    @Autowired
    private EkilexWordRepository wordRepository;

    private LexemeFromEkiLexService lexemeFromEkiLexService;

    @Before
    public void setUp() {
        this.lexemeFromEkiLexService = new LexemeFromEkiLexService(ekilexParadigmRepository, ekilexLexemeRepository, wordRepository);
    }

    @Test
    @Sql("classpath:sql/single-paradigm.sql")
    public void testSingleParadigm() {
        List<Lexeme> lexemeSet = lexemeFromEkiLexService.buildLexemesFromEkiLexDetails("ema");
        assertThat(lexemeSet).size().isEqualTo(1);
        Lexeme lexeme = lexemeSet.toArray(new Lexeme[]{})[0];
        assertThat(lexeme.getForms()).size().isEqualTo(3);
        assertThat(lexeme.getForms()).allMatch(getFormDeclinationTypePredicate("22"));
    }

    @Test
    @Sql("classpath:sql/multiple-paradigms.sql")
    public void testMultipleParadigms() {
        List<Lexeme> lexemeSet = lexemeFromEkiLexService.buildLexemesFromEkiLexDetails("koer");
        assertThat(lexemeSet)
                .hasSize(2)
                .areAtLeastOne(lc(lexeme -> lexeme.getPartOfSpeech().getPartOfSpeech().equalsIgnoreCase("noun")))
                .areAtLeastOne(lc(lexeme -> lexeme.getPartOfSpeech().getPartOfSpeech().equalsIgnoreCase("adjective")))
                .allSatisfy(lexeme -> assertThat(lexeme.getForms()).hasSize(4));

        for (Lexeme lexeme : lexemeSet) {
            assertThat(lexeme.getForms()).size().isEqualTo(4);
            assertThat(lexeme.getForms()).areAtLeast(2, new Condition<>(getFormDeclinationTypePredicate("22,23"), " has all necessary declinations"));
            assertThat(lexeme.getForms()).areAtLeastOne(new Condition<>(getFormDeclinationTypePredicate("22"), " has declination 22"));
            assertThat(lexeme.getForms()).areAtLeastOne(new Condition<>(getFormDeclinationTypePredicate("23"), " has declination 23"));
        }
    }

    private <T> Condition<? super T> lc(Predicate<T> predicate, Object... args) {
        return new Condition<>(predicate, "my condition", args);
    }

    private Predicate<Form> getFormDeclinationTypePredicate(String declinationType) {
        return f -> declinationType.equals(f.getDeclinationTypes());
    }

}