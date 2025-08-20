package ee.mkv.estonian.ekilex.web;

import ee.mkv.estonian.domain.EkilexRaw;
import ee.mkv.estonian.repository.EkilexRawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PersistingResponseConsumer implements ResponseConsumer {

    private final EkilexRawRepository ekilexRawRepository;

    /**
     * Consumes the response from a REST call and persists it to the database.
     *
     * @param response the response to consume
     * @param args     additional arguments, where args[0] is the endpoint and args[1] is the key
     */
    @Override
    public void consumeResponse(byte[] response, Object... args) {
        var endpoint = String.valueOf(args[0]);
        var key = String.valueOf(args[1]);
        EkilexRaw ekilexRaw = new EkilexRaw();
        ekilexRaw.setEndpoint(endpoint);
        ekilexRaw.setKey(key);
        ekilexRaw.setRawData(new String(response));
        ekilexRaw.setCreatedAt(Instant.now());
        ekilexRawRepository.save(ekilexRaw);
    }
}
