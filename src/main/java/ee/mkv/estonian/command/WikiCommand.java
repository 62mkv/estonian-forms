package ee.mkv.estonian.command;


import ee.mkv.estonian.service.ProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import picocli.CommandLine;

import java.io.IOException;
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

    @CommandLine.Option(names = {"-f", "--file"})
    private String sourceFile;

    public WikiCommand(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @Override
    public void run() {
        try {
            if (lemma != null && partOfSpeech != null) {
                processSingleOption();
            } else {
                processFile();
            }
            log.info("Finished processing command 'wikidata'");
        } catch (Exception e) {
            log.error("Exception while running command: {}", e.getMessage(), e);
        } finally {
            System.exit(0);
        }
    }

    private void processFile() throws IOException, MediaWikiApiErrorException {
        log.info("Processing file: {}", sourceFile);
        processingService.processFile(sourceFile);
    }

    private void processSingleOption() throws MediaWikiApiErrorException, IOException {
        log.info("Starting processing command 'wikidata' with lemma-list: {} and part of speech: {}", Objects.requireNonNull(lemma), Objects.requireNonNull(partOfSpeech));
        processingService.processLemmas(lemma, partOfSpeech);
    }

}
