package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.LoanWord;
import ee.mkv.estonian.repository.LoanWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LexemeRejectionService {

    private final LoanWordRepository loanWordRepository;

    public void rejectLexemeAsLoanWord(Lexeme lexeme) {
        log.info("Rejecting lexeme {}", lexeme);
        var entity = new LoanWord();
        entity.setLexeme(lexeme);
        loanWordRepository.save(entity);

    }
}
