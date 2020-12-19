package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.LexemeToEkiLexMapping;
import ee.mkv.estonian.repository.*;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    @Autowired
    private LexemeToEkilexMappingRepository mappingRepository;

    @Autowired
    private LexemeRepository lexemeRepository;

    @Autowired
    private FormRepository formRepository;

    private LexemeFromEkiLexService lexemeFromEkiLexService;
    private LexemePersistingService lexemePersistingService;

    @Before
    public void setUp() {
        this.lexemeFromEkiLexService = new LexemeFromEkiLexService(ekilexParadigmRepository, ekilexLexemeRepository, wordRepository, mappingRepository);
        this.lexemePersistingService = new LexemePersistingService(lexemeRepository, formRepository, mappingRepository);
    }

    @Test
    @Sql("classpath:sql/single-paradigm.sql")
    public void testSingleParadigm() {
        final List<LexemeToEkiLexMapping> lexemeToEkiLexMappings = lexemeFromEkiLexService.buildLexemesFromEkiLexDetails("ema");

        assertThat(lexemeToEkiLexMappings)
                .hasSize(1)
                .allSatisfy(mapping -> {
                    assertThat(mapping.getLexeme()).isNotNull();
                    assertThat(mapping.getEkilexWord()).isNotNull();
                    assertThat(mapping.getPartOfSpeech()).isNotNull();
                });

        List<Lexeme> lexemeSet = lexemeToEkiLexMappings
                .stream()
                .map(LexemeToEkiLexMapping::getLexeme)
                .collect(Collectors.toList());
        assertThat(lexemeSet).size().isEqualTo(1);
        Lexeme lexeme = lexemeSet.stream().findFirst().get();
        assertThat(lexeme.getForms()).size().isEqualTo(3);
        assertThat(lexeme.getForms()).allMatch(getFormDeclinationTypePredicate("22"));
    }

    @Test
    @Sql("classpath:sql/multiple-paradigms.sql")
    public void testMultipleParadigms() {
        List<Lexeme> lexemeSet = lexemeFromEkiLexService.buildLexemesFromEkiLexDetails("koer")
                .stream()
                .map(LexemeToEkiLexMapping::getLexeme)
                .collect(Collectors.toList());
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

    @Test
    @Sql("classpath:sql/single-paradigm.sql")
    public void testSingleParadigmWithSaving() {
        assertThat(mappingRepository.findAll()).isEmpty();
        assertThat(formRepository.findAll()).isEmpty();
        assertThat(lexemeRepository.findAll()).isEmpty();

        final List<LexemeToEkiLexMapping> lexemeToEkiLexMappings = lexemeFromEkiLexService.buildLexemesFromEkiLexDetails("ema");
        for (LexemeToEkiLexMapping mapping : lexemeToEkiLexMappings) {
            lexemePersistingService.save(mapping);
        }

        final Iterable<Lexeme> lexemes = lexemeRepository.findAll();

        assertThat(lexemes)
                .hasSize(1)
                .allMatch(lexeme -> {
                    return lexeme.getForms().size() == 3
                            && lexeme.getId() > 0
                            && lexeme.getPartOfSpeech().getPartOfSpeech().equals("Noun")
                            && lexeme.getLemma().getRepresentation().equals("ema")
                            && lexeme.getWikidataId() == null;
                });

        Lexeme lexeme = (Lexeme) ((Collection) lexemes).stream().findFirst().get();

        final Iterable<LexemeToEkiLexMapping> ekiLexMappings = mappingRepository.findAll();
        assertThat(ekiLexMappings)
                .hasSize(1)
                .allMatch(mapping -> {
                    return mapping.getLexeme().equals(lexeme)
                            && mapping.getId() > 0
                            && mapping.getPartOfSpeech().getPartOfSpeech().equals("Noun")
                            && mapping.getEkilexWord().getId().equals(1000L);
                });

        LexemeToEkiLexMapping oldMapping = (LexemeToEkiLexMapping) ((Collection) ekiLexMappings).stream().findFirst().get();

        final List<LexemeToEkiLexMapping> newMappings = lexemeFromEkiLexService.buildLexemesFromEkiLexDetails("ema");
        assertThat(newMappings)
                .hasSize(1)
                .allMatch(mapping -> mapping.equals(oldMapping));

        for (LexemeToEkiLexMapping mapping : newMappings) {
            lexemePersistingService.save(mapping);
        }

        assertThat(lexemeRepository.findAll()).hasSize(1);
        assertThat(mappingRepository.findAll()).hasSize(1);
    }

    private <T> Condition<? super T> lc(Predicate<T> predicate, Object... args) {
        return new Condition<>(predicate, "my condition", args);
    }

    private Predicate<Form> getFormDeclinationTypePredicate(String declinationType) {
        return f -> declinationType.equals(f.getDeclinationTypes());
    }

}