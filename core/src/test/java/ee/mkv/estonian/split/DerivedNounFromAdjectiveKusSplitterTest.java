package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.repository.FormRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DerivedNounFromAdjectiveKusSplitterTest {

    private FormRepository formRepository;
    private DerivedNounFromAdjectiveKusSplitter splitter;

    @BeforeEach
    void setUp() {
        formRepository = mock(FormRepository.class);
        splitter = new DerivedNounFromAdjectiveKusSplitter(formRepository);
    }

    @Test
    void testGetSuffix() {
        assertEquals("kus", splitter.getSuffix());
    }

    @NotNull
    private static FormTypeCombination getFormTypeCombination(String ekiRepresentation) {
        FormTypeCombination ftc = new FormTypeCombination();
        ftc.setEkiRepresentation(ekiRepresentation);
        return ftc;
    }

    @NotNull
    private static Representation getRepresentation(String representation) {
        Representation rep = new Representation();
        rep.setRepresentation(representation);
        return rep;
    }

    @Test
    void testGetBase() {
        String representation = "ilus_kus";
        String base = splitter.getBase(representation);
        assertEquals("ilus_", base);
    }

    @Test
    void testGetBaseWithExactSuffix() {
        String representation = "kus";
        String base = splitter.getBase(representation);
        assertEquals("", base);
    }

    @NotNull
    private static PartOfSpeech getPartOfSpeech(String ekiCodes) {
        PartOfSpeech pos = new PartOfSpeech();
        pos.setEkiCodes(ekiCodes); // Verb code
        return pos;
    }

    @Test
    void testFormTypeMatchesTrue() {
        final FormTypeCombination ftc = getFormTypeCombination("SgN");
        Form form = new Form();
        form.setFormTypeCombination(ftc);
        assertTrue(splitter.formTypeMatches(form));
    }

    @Test
    void testFormTypeMatchesFalse() {
        FormTypeCombination ftc = getFormTypeCombination("PlN");
        Form form = new Form();
        form.setFormTypeCombination(ftc);
        assertFalse(splitter.formTypeMatches(form));
    }

    @Test
    void testTrySplitLexemeReturnsEmptyIfNotNounOrNoSuffix() {
        // Lexeme with non-noun part of speech and lemma not ending with 'kus'
        Lexeme lexeme = new Lexeme();
        PartOfSpeech pos = getPartOfSpeech("XV");
        lexeme.setPartOfSpeech(pos);
        final Representation rep = getRepresentation("midagi");
        lexeme.setLemma(rep);
        assertTrue(splitter.trySplitLexeme(lexeme).isEmpty());
    }

    @Test
    void testCanProcessFalseIfNotNounOrNoSuffix() {
        Lexeme lexeme = new Lexeme();
        PartOfSpeech pos = getPartOfSpeech("XV");
        lexeme.setPartOfSpeech(pos);
        final Representation rep = getRepresentation("midagi");
        lexeme.setLemma(rep);
        assertFalse(splitter.canProcess(lexeme));
    }

    @Test
    void testCanProcessTrue() {
        final PartOfSpeech pos = getPartOfSpeech("GS");
        final Representation rep = getRepresentation("spartakus");
        Lexeme lexeme = new Lexeme();
        lexeme.setPartOfSpeech(pos);
        lexeme.setLemma(rep);
        assertTrue(splitter.canProcess(lexeme));
    }

    @Test
    void testTrySplitLexemeReturnsEmptyIfNoMatchingForm() {
        // Lexeme with noun part of speech and lemma ending with 'kus', but repository returns no matching forms
        Lexeme lexeme = new Lexeme();
        final PartOfSpeech pos = getPartOfSpeech("GS");
        lexeme.setPartOfSpeech(pos);
        Representation rep = getRepresentation("ilus_kus");
        lexeme.setLemma(rep);
        when(formRepository.findWhereRepresentationIn(any())).thenReturn(List.of());
        assertTrue(splitter.trySplitLexeme(lexeme).isEmpty());
    }

    @Test
    void testTrySplitLexemeReturnsCompoundWordIfMatchingForm() {
        // Lexeme with noun part of speech and lemma ending with 'kus', repository returns matching adjective form
        Lexeme lexeme = new Lexeme();
        final PartOfSpeech pos = getPartOfSpeech("GS");
        lexeme.setPartOfSpeech(pos);
        Representation rep = getRepresentation("ilus_kus");
        lexeme.setLemma(rep);
        // Create matching adjective form
        Form baseForm = new Form();
        Representation baseRep = getRepresentation("ilus_");
        baseForm.setRepresentation(baseRep);
        final FormTypeCombination ftc = getFormTypeCombination("SgN");
        baseForm.setFormTypeCombination(ftc);
        Lexeme adjLexeme = new Lexeme();
        PartOfSpeech adjPos = getPartOfSpeech("AH");
        adjLexeme.setPartOfSpeech(adjPos);
        baseForm.setLexeme(adjLexeme);
        when(formRepository.findWhereRepresentationIn(any())).thenReturn(List.of(baseForm));
        var resultOpt = splitter.trySplitLexeme(lexeme);
        assertTrue(resultOpt.isPresent());
        var result = resultOpt.get();
        assertEquals(CompoundRule.DERIVED_NAME_FROM_ADJECTIVE.getId(), result.getCompoundRule());
        assertFalse(result.isRejected());
        assertEquals(lexeme, result.getLexeme());
        assertNotNull(result.getComponents());
        assertEquals(1, result.getComponents().size());
        CompoundWordComponent component = result.getComponents().get(0);
        assertEquals(baseForm, component.getForm());
        assertEquals(0, component.getComponentIndex());
        // Optionally check componentStartsAt if relevant
    }
}
