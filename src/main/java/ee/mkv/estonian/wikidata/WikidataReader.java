package ee.mkv.estonian.wikidata;

import ee.mkv.estonian.domain.Lexeme;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleIRI;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WikidataReader {

    private final static Function<Lexeme, String> LEMMA_QUERY = (lexeme) -> String.format("SELECT ?lexeme ?lemma WHERE {\n" +
            "  ?lexeme dct:language wd:Q9072;wikibase:lexicalCategory wd:%s;wikibase:lemma ?lemma.\n" +
            "  FILTER (STR(?lemma)=\"%s\")}", lexeme.getPartOfSpeech().getWikidataCode(), lexeme.getLemma().getRepresentation());

    private final static Function<String, String> FORM_COUNT_QUERY = (lexemeId) -> String.format(
            "SELECT (count(*) as ?count) WHERE { \n" +
                    "       wd:%s ontolex:lexicalForm ?form. \n" +
                    "       ?form ontolex:representation ?representation. \n" +
                    "       ?form wikibase:grammaticalFeature ?feature;\n" +
                    "}", lexemeId);

    private final QueryExecutor executor;

    public WikidataReader(QueryExecutor queryExecutor) {
        this.executor = queryExecutor;
    }

    public Optional<String> checkLexeme(Lexeme lexeme) {
        String query = LEMMA_QUERY.apply(lexeme);
        final Long lexemeId = lexeme.getId();

        try (TupleQueryResult lexemesResult = executor.executeQuery(query)) {
            List<BindingSet> results = lexemesResult.stream().collect(Collectors.toList());
            if (results.isEmpty()) {
                log.info("Lexeme {}:{} not found", lexeme.getLemma().getRepresentation(), lexeme.getPartOfSpeech().getPartOfSpeech());
                return Optional.empty();
            }

            if (results.size() > 1) {
                throw new RuntimeException("More than one potential candidate found!");
            }

            final SimpleIRI lexemeIRI = (SimpleIRI) results.get(0).getValue("lexeme");

            return Optional.ofNullable(lexemeIRI.getLocalName());
        } catch (Throwable error) {
            log.error("Error while checking lexeme {}: {}", lexemeId, error.getMessage(), error);
            throw new RuntimeException(error);
        }
    }

    public Integer countLexemeForms(String id) {
        String query = FORM_COUNT_QUERY.apply(id);

        try (TupleQueryResult lexemesResult = executor.executeQuery(query)) {
            List<BindingSet> results = lexemesResult.stream().collect(Collectors.toList());
            if (results.isEmpty()) {
                throw new RuntimeException(String.format("Couldn't count forms for lexeme id %s", id));
            } else {
                Value value = results.get(0).getValue("count");
                return Integer.parseInt(value.stringValue());
            }
        } catch (IOException error) {
            log.error("Error while counting forms for lexeme id {}", id, error);
            throw new RuntimeException(error);
        }
    }
}
