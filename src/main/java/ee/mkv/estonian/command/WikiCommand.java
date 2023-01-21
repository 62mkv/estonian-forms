package ee.mkv.estonian.command;


import ee.mkv.estonian.service.ProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.Objects;

@Component
@CommandLine.Command(name = "wikidata")
@Slf4j
public class WikiCommand implements Runnable {

    private final ProcessingService processingService;

    @CommandLine.Option(names = {"-l", "--lemma-list"})
    private String lemma;

    @CommandLine.Option(names = {"-p", "--partOfSpeech"})
    private String partOfSpeech;

    public WikiCommand(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @Override
    public void run() {
        try {
            log.info("Starting processing command 'wikidata' with lemma-list: {} and part of speech: {}", Objects.requireNonNull(lemma), Objects.requireNonNull(partOfSpeech));
            processingService.processLemmas(lemma, partOfSpeech);
            log.info("Finished processing command 'wikidata'");
        } catch (Exception e) {
            log.error("Exception while running command: {}", e.getMessage(), e);
        } finally {
            System.exit(0);
        }
    }

}
