package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundWord;
import ee.mkv.estonian.domain.Lexeme;

import java.util.Optional;

public interface LexemeSplitter {

    int getPriority();

    Optional<CompoundWord> trySplitLexeme(Lexeme lexeme);
}
