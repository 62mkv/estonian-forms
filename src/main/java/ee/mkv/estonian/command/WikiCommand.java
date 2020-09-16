package ee.mkv.estonian.command;


import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.PartOfSpeechRepository;
import ee.mkv.estonian.repository.RepresentationsRepository;
import ee.mkv.estonian.wikidata.WikidataUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
@CommandLine.Command(name = "wikidata")
@Slf4j
public class WikiCommand implements Runnable {

    private final WikidataUploader wikidataUploader;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final RepresentationsRepository representationsRepository;

    @CommandLine.Option(names = {"-l", "--lemma"})
    private String lemma;

    @CommandLine.Option(names = {"-p", "--partOfSpeech"})
    private String partOfSpeech;

    public WikiCommand(WikidataUploader wikidataUploader, PartOfSpeechRepository partOfSpeechRepository, RepresentationsRepository representationsRepository) {
        this.wikidataUploader = wikidataUploader;
        this.partOfSpeechRepository = partOfSpeechRepository;
        this.representationsRepository = representationsRepository;
    }

    @Override
    public void run() {
        log.info("Starting processing command 'wikidata'");
        Lexeme lexeme = new Lexeme();
        partOfSpeechRepository
                .findByPartOfSpeech(partOfSpeech)
                .ifPresent(lexeme::setPartOfSpeech);

        representationsRepository
                .findByRepresentation(lemma)
                .ifPresent(lexeme::setLemma);

        final Optional<String> wikidataId = wikidataUploader.checkLexeme(lexeme);

        if (wikidataId.isPresent()) {
            wikidataId.ifPresent(id -> log.info("Lexeme wikidata id: {}", id));
        } else {
            log.info("No such lexeme has been found");
        }
        log.info("Finished processing command 'wikidata'");
    }
}
