package ee.mkv.estonian.command;


import ee.mkv.estonian.config.WikidataProperties;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.service.LexemeDbService;
import ee.mkv.estonian.wikidata.WikidataReader;
import ee.mkv.estonian.wikidata.WikidataUploader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import picocli.CommandLine;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@CommandLine.Command(name = "wikidata")
@Slf4j
public class WikiCommand implements Runnable {

    private final WikidataReader wikidataReader;
    private final WikidataUploader wikidataUploader;
    private final LexemeDbService lexemeDbService;
    private final WikidataProperties.WikidataSite site;

    @CommandLine.Option(names = {"-l", "--lemma"})
    private String lemma;

    @CommandLine.Option(names = {"-p", "--partOfSpeech"})
    private String partOfSpeech;

    public WikiCommand(WikidataReader wikidataReader, WikidataUploader wikidataUploader, LexemeDbService lexemeDbService, WikidataProperties properties) {
        this.wikidataReader = wikidataReader;
        this.wikidataUploader = wikidataUploader;
        this.lexemeDbService = lexemeDbService;
        this.site = properties.getSite();
    }

    @Override
    public void run() {
        try {
            log.info("Starting processing command 'wikidata'");
            log.info("Looking for lexemes in Db with lemma = [{}] and part of speech = [{}]", Objects.requireNonNull(lemma), Objects.requireNonNull(partOfSpeech));
            final List<Lexeme> lexemeList = lexemeDbService.getLexemes(lemma, partOfSpeech);
            long formCount = lexemeList.stream().map(Lexeme::getForms).flatMap(Set::stream).count();
            log.info("Found {} lexemes in Db, with {} forms", lexemeList.size(), formCount);
            if (lexemeList.size() == 1) {
                Lexeme lexeme = lexemeList.get(0);
                if (StringUtils.isNotBlank(lexeme.getWikidataId())) {
                    log.info("DB Lexeme already has wikidataId, that means it was already added");
                } else {
                    processLexeme(lexeme);
                }
            } else {
                log.error("More than one Db lexeme is found, processing halted due to ambiguity");
            }
            log.info("Finished processing command 'wikidata'");
        } catch (Exception e) {
            log.error("Exception while running command: {}", e.getMessage(), e);
        } finally {
            System.exit(0);
        }
    }

    private void processLexeme(Lexeme lexeme) throws MediaWikiApiErrorException, IOException {
        if (lexeme.getForms().isEmpty()) {
            log.warn("Not going to create lexemes without forms! {}", lexeme);
            return;
        }

        final Optional<String> wikidataId = wikidataReader.checkLexeme(lexeme);

        String newId = "";
        if (wikidataId.isPresent()) {
            String id = wikidataId.get();
            log.info("Lexeme wikidata id: {}", id);
            final Integer wdFormCount = wikidataReader.countLexemeForms(id);
            if (wdFormCount > 0) {
                log.warn("This WD lexeme has some forms already, so we should skip it!");
            } else {
                log.info("0 forms found, go ahead and create full lexeme!");
                newId = wikidataUploader.addFormsToLexeme(id, lexeme);
            }
        } else {
            log.info("No corresponding lexeme has been found at Wikidata");
            newId = wikidataUploader.createLexemeWithForms(lexeme);
        }

        if (site.equals(WikidataProperties.WikidataSite.PROD)) {
            log.info("Saving wikidata id for the lexeme as {}", newId);
            lexemeDbService.updateWikidataIdOnLexeme(lexeme, newId);
        } else {
            log.info("WD Lexeme id is {}", newId);
        }
    }
}
