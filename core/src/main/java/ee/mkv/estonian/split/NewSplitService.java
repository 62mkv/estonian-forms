package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.CompoundWord;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.CompoundWordRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewSplitService {

    private final LexemeRepository lexemeRepository;
    private final CompoundWordRepository compoundWordRepository;
    private final Collection<LexemeSplitter> lexemeSplitters;
    private final PlatformTransactionManager transactionManager;
    private final CommandCoordinator commandCoordinator;

    public void runSplitService() {
        boolean continueProcessing;
        do {
            final Lexeme lastLexeme = getLexeme();

            log.info("Last lexeme: {}", lastLexeme);
            continueProcessing = commandCoordinator.runCommand(lastLexeme);
        } while (continueProcessing);
    }

    private Lexeme getLexeme() {
        AtomicBoolean foundNewCompounds = new AtomicBoolean(true);
        Lexeme lastLexeme = null;
        while (foundNewCompounds.get()) {
            foundNewCompounds.set(false);
            TransactionStatus transactionStatus = transactionManager.getTransaction(TransactionDefinition.withDefaults());
            try {
                for (Lexeme lexeme : lexemeRepository.findNextUnsplitCandidates(1)) {
                    lastLexeme = lexeme;
                    log.info("Examining lexeme {}", lexeme);
                    tryFindCompoundWord(lexeme).ifPresent(compoundWord -> {
                        log.info("CompoundWord built for lexeme {}: {}", lexeme, compoundWord);
                        compoundWordRepository.save(compoundWord);
                        compoundWordRepository.flush();
                        foundNewCompounds.set(true);
                    });
                }
                transactionManager.commit(transactionStatus);
            } catch (Exception e) {
                log.error("Exception occurred", e);
                transactionManager.rollback(transactionStatus);
            }
        }
        return lastLexeme;
    }

    private Optional<CompoundWord> tryFindCompoundWord(Lexeme lexeme) {
        return lexemeSplitters.stream()
                .sorted(Comparator.comparing(LexemeSplitter::getPriority))
                .map(lexemeSplitter -> lexemeSplitter.trySplitLexeme(lexeme))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }


}
