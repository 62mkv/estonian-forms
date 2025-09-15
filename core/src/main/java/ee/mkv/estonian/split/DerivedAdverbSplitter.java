package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundRule;
import ee.mkv.estonian.domain.CompoundWord;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DerivedAdverbSplitter implements LexemeSplitter {

    private final FormRepository formRepository;

    @Override
    public boolean canProcess(Lexeme lexeme) {
        return lexeme.getPartOfSpeech().isAdverb();
    }

    @Override
    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        return formRepository.findWhereRepresentationIn(Set.of(lexeme.getLemma().getRepresentation()))
                .stream()
                .filter(form -> !Objects.equals(form.getLexeme().getId(), lexeme.getId()))
                .filter(form -> form.getLexeme().isName())
                .findFirst()
                .map(form -> SplitUtils.getCompoundWord(lexeme, form, CompoundRule.DERIVED_ADVERB));
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
