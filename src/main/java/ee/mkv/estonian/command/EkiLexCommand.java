package ee.mkv.estonian.command;

import ee.mkv.estonian.domain.EkilexWord;
import ee.mkv.estonian.ekilex.EkiLexRetrievalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "ekilex")
@Slf4j
public class EkiLexCommand implements Runnable {
    private final EkiLexRetrievalService retrievalService;

    @CommandLine.Option(names = {"-l", "--lemma-list"})
    private String lemma;
    @CommandLine.Option(names = {"-p", "--partOfSpeech"})
    private String partOfSpeech;

    public EkiLexCommand(EkiLexRetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @Override
    public void run() {
        EkilexWord ekilexWord = retrievalService.retrieveByNextWordId(false);
        log.info("Retrieved word {}:{}", ekilexWord.getId(), ekilexWord.getBaseForm().getRepresentation());
    }
}
