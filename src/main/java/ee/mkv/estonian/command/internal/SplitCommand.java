package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.CompoundWordRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.split.FindFormsForFullNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import picocli.CommandLine;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@CommandLine.Command(name = "split-words")
@RequiredArgsConstructor
@Slf4j
public class SplitCommand extends HelpAwarePicocliCommand {
    private final LexemeRepository lexemeRepository;
    private final CompoundWordRepository compoundWordRepository;
    private final FindFormsForFullNameService findFormsForFullNameService;
    private final PlatformTransactionManager transactionManager;

    @Override
    public ExitStatus call() throws Exception {

        AtomicBoolean foundNewCompounds = new AtomicBoolean(true);
        while (foundNewCompounds.get()) {
            foundNewCompounds.set(false);
            TransactionStatus transactionStatus = transactionManager.getTransaction(TransactionDefinition.withDefaults());
            try {
                for (Lexeme lexeme : lexemeRepository.findNextUnsplitCandidates(1, 1)) {
                    log.info("Examining lexeme {}", lexeme);
                    findFormsForFullNameService.findFormsForSplittings(lexeme).ifPresent(compoundWord -> {
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

        return ExitStatus.OK;
    }


}
