package ee.mkv.estonian.wikidata;

import ee.mkv.estonian.config.WikidataProperties;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.repository.LexemeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
@ConditionalOnProperty("wikidata.site")
public class WikiUploadService {

    private static final Set<String> NAMES_AND_VERBS = initSet("Noun", "Verb", "Adjective");

    private static Set<String> initSet(String... members) {
        return new HashSet<>(Arrays.asList(members));
    }

    private final WikidataReader wikidataReader;
    private final WikidataUploader wikidataUploader;
    private final LexemeRepository lexemeRepository;
    private final WikidataProperties.WikidataSite site;

    public WikiUploadService(WikidataReader wikidataReader, WikidataUploader wikidataUploader, LexemeRepository lexemeRepository, WikidataProperties properties) {
        this.wikidataReader = wikidataReader;
        this.wikidataUploader = wikidataUploader;
        this.lexemeRepository = lexemeRepository;
        this.site = properties.getSite();
    }

    @Transactional
    public void uploadLexeme(Lexeme lexeme) {
        log.info("Uploading lexeme {} with lemma {}", lexeme.getId(), lexeme.getLemma().getRepresentation());
        try {
            processLexeme(lexemeRepository.findById(lexeme.getId()).get());
        } catch (Exception e) {
            log.error("Exception while processing lexeme {}/{}: {}", lexeme.getId(), lexeme.getLemma().getRepresentation(), e.getMessage(), e);
        }
    }

    private void processLexeme(Lexeme lexeme) throws MediaWikiApiErrorException, IOException {
        if (lexeme.getForms().isEmpty() && mustHaveForms(lexeme.getPartOfSpeech())) {
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
            lexeme.setWikidataId(newId);
            lexemeRepository.save(lexeme);
        } else {
            log.info("WD Lexeme id is {}", newId);
        }
    }

    private boolean mustHaveForms(PartOfSpeech partOfSpeech) {
        return NAMES_AND_VERBS.contains(partOfSpeech.getPartOfSpeech());
    }
}
