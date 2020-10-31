package ee.mkv.estonian.wikidata;

import ee.mkv.estonian.config.WikidataProperties;
import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.FormType;
import ee.mkv.estonian.domain.Lexeme;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WikidataUploader {

    private static final String LANGUAGE_CODE = "et";
    private final ItemIdValue languageId;

    private final WikibaseDataFetcher fetcher;
    private final WikibaseDataEditor editor;
    private final String siteIri;
    private final WikidataProperties.WikidataSite site;

    public WikidataUploader(WikibaseDataFetcher fetcher, WikibaseDataEditor editor, WikidataProperties properties) {
        this.fetcher = fetcher;
        this.editor = editor;
        this.siteIri = properties.getSite().getIri();
        this.site = properties.getSite();
        this.languageId = createItemId("Q9072");
    }

    /**
     * Will process the given lexeme by creating lexeme and it's forms in the WD; will apply newly retrieved id to the lexeme
     *
     * @param lexeme
     * @return id of the newly created Lexeme
     */
    public String createLexemeWithForms(Lexeme lexeme) throws IOException, MediaWikiApiErrorException {
        if (lexeme.getForms().isEmpty()) {
            throw new RuntimeException("We'll not pollute Wikidata with empty lexemes!");
        }

        final ItemIdValue lexicalCategory = createItemId(lexeme.getPartOfSpeech().getWikidataCode());

        LexemeDocument lexemeDocument = Datamodel.makeLexemeDocument(
                LexemeIdValue.NULL,
                lexicalCategory,
                languageId,
                Collections.singletonList(Datamodel.makeMonolingualTextValue(lexeme.getLemma().getRepresentation(), LANGUAGE_CODE)));

        lexemeDocument = getLexemeDocumentWithForms(lexeme, lexemeDocument);

        lexemeDocument = editor.createLexemeDocument(lexemeDocument,
                String.format("Bot creating lexeme '%s'", lexeme.getLemma().getRepresentation()),
                null
        );

        return lexemeDocument.getEntityId().getId();
    }

    /**
     * Will create forms of given lexeme under WD lexeme with given id; will apply this id to the lexeme
     *
     * @param id
     * @param lexeme
     * @return id of the lexeme
     */
    public String addFormsToLexeme(String id, Lexeme lexeme) throws MediaWikiApiErrorException, IOException {
        if (site.equals(WikidataProperties.WikidataSite.TEST)) {
            throw new RuntimeException("Adding forms to existing lexeme is only possible on PROD WikiData");
        }

        LexemeDocument lexemeDocument = (LexemeDocument) fetcher.getEntityDocument(id);

        lexemeDocument = getLexemeDocumentWithForms(lexeme, lexemeDocument);

        lexemeDocument = editor.updateLexemeDocument(lexemeDocument,
                String.format("Bot adding forms to lexeme '%s'", lexeme.getLemma().getRepresentation()),
                null
        );

        return lexemeDocument.getEntityId().getId();
    }

    private LexemeDocument getLexemeDocumentWithForms(Lexeme lexeme, LexemeDocument lexemeDocument) {
        final List<Form> forms = lexeme
                .getForms()
                .stream()
                .filter(form -> !"Rpl".equalsIgnoreCase(form.getFormTypeCombination().getEkiRepresentation()))
                .sorted(Comparator.comparing(form -> form.getFormTypeCombination().getId()))
                .collect(Collectors.toList());

        for (Form form : forms) {
            FormDocument formDocument = createFormDocument(lexemeDocument, form);
            lexemeDocument = lexemeDocument.withForm(formDocument);
        }
        return lexemeDocument;
    }

    private ItemIdValue createItemId(String id) {
        return Datamodel.makeItemIdValue(id, siteIri);
    }

    private FormDocument createFormDocument(LexemeDocument lexemeDocument, Form form) {
        final List<ItemIdValue> features = form.getFormTypeCombination().getFormTypes().stream()
                .map(FormType::getWikidataCode)
                .filter(StringUtils::isNotBlank) // skipping features such as "Rpl", which do not have a WD id
                .map(this::applyTestification)
                .distinct()
                .map(this::createItemId)
                .collect(Collectors.toList());
        final List<MonolingualTextValue> representations = Collections.singletonList(
                Datamodel.makeMonolingualTextValue(form.getRepresentation().getRepresentation(), LANGUAGE_CODE)
        );
        return lexemeDocument.createForm(
                representations,
                features
        );
    }

    private String applyTestification(String id) {
        if (site.equals(WikidataProperties.WikidataSite.TEST)) {
            return "Q42";
        }

        return id;
    }
}
