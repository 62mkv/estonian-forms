package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractDerivedNounFromAdjectiveSplitter implements LexemeSplitter {
    protected final FormRepository formRepository;

    protected abstract String getSuffix();

    protected abstract boolean formTypeMatches(Form form);

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        log.info("Trying to split lexeme {} with {}", lexeme, this.getClass().getSimpleName());
        if (isName(lexeme) && lexeme.getLemma().getRepresentation().endsWith(getSuffix())) {
            String representation = lexeme.getLemma().getRepresentation();
            String base = getBase(representation);
            log.info("Looking for adjective forms for base {}, found", base);
            List<Form> forms = formRepository.findWhereRepresentationIn(Set.of(base));
            log.info("Found {} forms: [{}]", forms.size(), forms);
            return forms
                    .stream()
                    .filter(this::formTypeMatches)
                    .filter(form -> form.getLexeme().getPartOfSpeech().getEkiCodes().contains(InternalPartOfSpeech.ADJECTIVE.getEkiCodes()))
                    .findFirst()
                    .map(form -> getCompoundWord(lexeme, form));
        }
        return Optional.empty();
    }

    @Override
    public boolean canProcess(Lexeme lexeme) {
        return lexeme.getPartOfSpeech().isNoun() && lexeme.getLemma().getRepresentation().endsWith(getSuffix());
    }

    protected abstract String getBase(String representation);

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