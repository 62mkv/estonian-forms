package ee.mkv.estonian.ekilex.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.mkv.estonian.domain.EkilexRaw;
import ee.mkv.estonian.repository.EkilexRawRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterceptingRestTemplate implements RestOperations {

    private static final Duration MAX_AGE = Duration.ofDays(14);

    @Delegate
    private final RestTemplate restTemplate;
    private final Optional<ResponseConsumer> responseConsumer;
    private final Map<Class<?>, HttpMessageConverterExtractor<?>> messageConverterExtractors = new java.util.HashMap<>();
    private final EkilexRawRepository ekilexRawRepository;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
        var key = String.valueOf(uriVariables[0]);
        var existingRecords = ekilexRawRepository.findByEndpointAndKey(url, key);
        var latestRecord = existingRecords.stream()
                .max(Comparator.comparing(EkilexRaw::getCreatedAt));
        // If the latest record is less than 14 days old, do not persist the new response
        if (latestRecord.isPresent()) {
            var ekilexRaw = latestRecord.get();
            if (ekilexRaw.getCreatedAt().isAfter(Instant.now().minus(MAX_AGE))) {
                log.info("Using cached response for endpoint: {}, key: {}", url, key);
                return objectMapper.readValue(ekilexRaw.getRawData(), responseType);
            }
        }

        RequestCallback requestCallback = restTemplate.acceptHeaderRequestCallback(responseType);
        ResponseExtractor<T> responseExtractor = getResponseExtractor(responseType, url, String.valueOf(uriVariables[0]));
        return restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }

    private <T> ResponseExtractor<T> getResponseExtractor(Class<T> responseType, String endpoint, String key) {
        final HttpMessageConverterExtractor httpMessageConverterExtractor =
                messageConverterExtractors.computeIfAbsent(responseType, t -> new HttpMessageConverterExtractor<>(t, restTemplate.getMessageConverters()));

        return new DummyResponseExtractor<>(httpMessageConverterExtractor, endpoint, key);
    }

    private static class ClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse original;
        private final InputStream body;

        public ClientHttpResponseWrapper(ClientHttpResponse original, InputStream body) {
            this.original = original;
            this.body = body;
        }

        @Override
        public InputStream getBody() {
            return body;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return original.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return original.getStatusText();
        }

        @Override
        public void close() {
            original.close();
        }

        @Override
        public HttpHeaders getHeaders() {
            return original.getHeaders();
        }
    }

    private class DummyResponseExtractor<T> implements ResponseExtractor<T> {
        private final ResponseExtractor<T> delegate;
        private final String endpoint;
        private final String key;

        public DummyResponseExtractor(ResponseExtractor<T> delegate, String endpoint, String key) {
            this.delegate = delegate;
            this.endpoint = endpoint;
            this.key = key;
        }

        @Override
        public T extractData(@NotNull ClientHttpResponse response) throws IOException {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(response.getBody())) {
                if (response.getStatusCode().is2xxSuccessful()) {
                    // log the response body
                    bufferedInputStream.mark(Integer.MAX_VALUE); // Mark the current position
                    String body = new String(bufferedInputStream.readAllBytes());
                    log.info("Successfully retrieved data from EkiLex: {}", body);
                    responseConsumer.ifPresent(consumer -> consumer.consumeResponse(body.getBytes(), endpoint, key));
                    // Reset the stream to allow further processing
                    bufferedInputStream.reset();
                }
                return delegate.extractData(new ClientHttpResponseWrapper(response, bufferedInputStream));
            }
        }
    }

}
