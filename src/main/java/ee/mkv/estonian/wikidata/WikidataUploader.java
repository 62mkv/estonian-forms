package ee.mkv.estonian.wikidata;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.domain.Representation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.rdf4j.model.impl.SimpleIRI;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WikidataUploader {

    private final static Function<Lexeme, String> QUERY_LEXEME = (lexeme) -> String.format("SELECT ?lexeme ?lemma WHERE {\n" +
            "  ?lexeme dct:language wd:Q9072;wikibase:lexicalCategory wd:%s;wikibase:lemma ?lemma.\n" +
            "  FILTER (STR(?lemma)=\"%s\")}", lexeme.getPartOfSpeech().getWikidataCode(), lexeme.getLemma().getRepresentation());

    private final QueryExecutor executor;

    public WikidataUploader(QueryExecutor queryExecutor) {
        this.executor = queryExecutor;
    }

    public Optional<String> checkLexeme(Lexeme lexeme) {
        String query = QUERY_LEXEME.apply(lexeme);
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

    public String saveLexeme(Lexeme lexeme) {
        throw new NotImplementedException("Saving lexemes is not yet implemented");
    }
}
