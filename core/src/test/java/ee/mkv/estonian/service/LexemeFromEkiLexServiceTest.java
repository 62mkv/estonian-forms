package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.LexemeToEkiLexMapping;
import ee.mkv.estonian.repository.*;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("h2test")
@DataJpaTest
class LexemeFromEkiLexServiceTest {

    @Autowired
    private EkilexParadigmRepository ekilexParadigmRepository;

    @Autowired
    private EkilexFormRepository ekilexFormRepository;

    @Autowired
    private EkilexWordRepository ekilexWordRepository;

    @Autowired
    private LexemeToEkilexMappingRepository mappingRepository;

    @Autowired
    private LexemeRepository lexemeRepository;

    @Autowired
    private FormTypeCombinationRepository formTypeCombinationRepository;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private PartOfSpeechUserInputProvider partOfSpeechUserInputProvider;

    @Autowired
    private PartOfSpeechRepository partOfSpeechRepository;

    @Autowired
    private EkilexPartOfSpeechService partOfSpeechService;

    private LexemeFromEkiLexService lexemeFromEkiLexService;
    private LexemePersistingService lexemePersistingService;

    @BeforeEach
    void setUp() {
        this.lexemeFromEkiLexService = new LexemeFromEkiLexService(ekilexWordRepository, ekilexParadigmRepository, mappingRepository, formTypeCombinationRepository, partOfSpeechUserInputProvider, partOfSpeechRepository, partOfSpeechService);
        this.lexemePersistingService = new LexemePersistingService(lexemeRepository, formRepository, mappingRepository);
    }

    @Test
    @Sql("classpath:sql/single-paradigm.sql")
    void testSingleParadigm() {
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
                .toList();
        assertThat(lexemeSet).size().isEqualTo(1);
        Lexeme lexeme = lexemeSet.stream().findFirst().get();
        assertThat(lexeme.getForms()).size().isEqualTo(3);
        assertThat(lexeme.getForms()).allMatch(getFormDeclinationTypePredicate("22"));
    }

    @Test
    @Sql("classpath:sql/multiple-paradigms.sql")
    void testMultipleParadigms() {
        List<Lexeme> lexemeSet = lexemeFromEkiLexService.buildLexemesFromEkiLexDetails("koer")
                .stream()
                .map(LexemeToEkiLexMapping::getLexeme)
                .toList();
        assertThat(lexemeSet)
                .hasSize(2)
                .areAtLeastOne(lc(lexeme -> lexeme.getPartOfSpeech().getPartOfSpeechName().equalsIgnoreCase("noun")))
                .areAtLeastOne(lc(lexeme -> lexeme.getPartOfSpeech().getPartOfSpeechName().equalsIgnoreCase("adjective")))
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
    void testSingleParadigmWithSaving() {
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
                            && lexeme.getPartOfSpeech().getPartOfSpeechName().equals("Noun")
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
                            && mapping.getPartOfSpeech().getPartOfSpeechName().equals("Noun")
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