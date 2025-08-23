package ee.mkv.estonian.split;


import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DerivedNounFromAdjectiveSplitter implements LexemeSplitter {

    public static final String SUFFIX = "us";
    private final FormRepository formRepository;

    private static boolean formTypeMatches(Form form) {
        var ekiRepresentation = form.getFormTypeCombination().getEkiRepresentation();
        return ekiRepresentation.equals("SgG") || ekiRepresentation.equals("SgN");
    }

    @Override
    public int getPriority() {
        return 0;
    }

    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        log.info("Trying to split lexeme {} with {}", lexeme, this.getClass().getSimpleName());
        if (isName(lexeme) && lexeme.getLemma().getRepresentation().endsWith(SUFFIX)) {
            return buildCompoundWord(lexeme);
        }
        return Optional.empty();
    }

    private Optional<CompoundWord> buildCompoundWord(Lexeme lexeme) {
        String representation = lexeme.getLemma().getRepresentation();
        String base = representation.substring(0, representation.length() - 1);
        log.info("Looking for adjective forms for base {}, found", base);
        List<Form> forms = formRepository.findWhereRepresentationIn(Set.of(base));
        log.info("Found {} forms: [{}]", forms.size(), forms);
        return forms
                .stream()
                .filter(DerivedNounFromAdjectiveSplitter::formTypeMatches)
                .filter(form -> form.getLexeme().getPartOfSpeech().getEkiCodes().contains(InternalPartOfSpeech.ADJECTIVE.getEkiCodes()))
                .findFirst()
                .map(form -> getCompoundWord(lexeme, form));
    }

    private boolean isName(Lexeme lexeme) {
        return Objects.equals(InternalPartOfSpeech.fromEkiCodes(lexeme.getPartOfSpeech().getEkiCodes()), InternalPartOfSpeech.NOUN);
    }

    private CompoundWord getCompoundWord(Lexeme lexeme, Form form) {
        var result = new CompoundWord();
        result.setCompoundRule(CompoundRule.DERIVED_NAME_FROM_ADJECTIVE);
        result.setLexeme(lexeme);
        var component = new CompoundWordComponent();
        component.setForm(form);
        component.setComponentIndex(0);
        component.setComponentStartsAt(0);
        component.setCompoundWord(result);
        result.setComponents(List.of(component));
        return result;
    }

}
