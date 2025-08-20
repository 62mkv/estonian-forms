package ee.mkv.estonian.ekilex.web;

public interface ResponseConsumer {
    /**
     * Consumes the response from a REST call.
     *
     * @param response the response to consume
     */
    void consumeResponse(byte[] response, Object... args);
}
