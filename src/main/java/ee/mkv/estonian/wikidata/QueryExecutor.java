package ee.mkv.estonian.wikidata;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.rdf4j.http.client.RDF4JProtocolSession;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.impl.SimpleDataset;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executors;

@Component
public class QueryExecutor implements DisposableBean {
    private static final String BASE_URI = "https://query.wikidata.org/sparql";

    private final HttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(
                    RequestConfig
                            .custom()
                            .setCookieSpec(CookieSpecs.STANDARD)
                            .build()
            )
            .build();

    private final RDF4JProtocolSession session = new RDF4JProtocolSession(httpClient, Executors.newScheduledThreadPool(1));
    private final Dataset dataset = new SimpleDataset();

    public QueryExecutor() {
        session.setRepository(BASE_URI);
    }

    public TupleQueryResult executeQuery(String query) throws IOException {
        return session.sendTupleQuery(QueryLanguage.SPARQL, query, BASE_URI, dataset, false, 10);
    }

    @Override
    public void destroy() throws Exception {
        session.close();
        ((CloseableHttpClient) httpClient).close();
    }
}
