package ee.mkv.estonian.command;


import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.service.LexemeDbService;
import ee.mkv.estonian.wikidata.WikidataUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@CommandLine.Command(name = "wikidata")
@Slf4j
public class WikiCommand implements Runnable {

    private final WikidataUploader wikidataUploader;
    private final LexemeDbService lexemeDbService;

    @CommandLine.Option(names = {"-l", "--lemma"})
    private String lemma;

    @CommandLine.Option(names = {"-p", "--partOfSpeech"})
    private String partOfSpeech;

    public WikiCommand(WikidataUploader wikidataUploader, LexemeDbService lexemeDbService) {
        this.wikidataUploader = wikidataUploader;
        this.lexemeDbService = lexemeDbService;
    }

    @Override
    public void run() {
        log.info("Starting processing command 'wikidata'");
        log.info("Looking for lexemes in Db with lemma = [{}] and part of speech = [{}]", lemma, partOfSpeech);
        final List<Lexeme> lexemeList = lexemeDbService.getLexeme(lemma, partOfSpeech);
        long formCount = lexemeList.stream().map(Lexeme::getForms).flatMap(Set::stream).count();
        log.info("Found {} lexemes in Db, with {} forms", lexemeList.size(), formCount);
        for (Lexeme lexeme : lexemeList) {

            final Optional<String> wikidataId = wikidataUploader.checkLexeme(lexeme);

            if (wikidataId.isPresent()) {
                wikidataId.ifPresent(id -> log.info("Lexeme wikidata id: {}", id));
            } else {
                log.info("No such lexeme has been found");
            }
        }
        log.info("Finished processing command 'wikidata'");
    }

}
