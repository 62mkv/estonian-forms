package ee.mkv.estonian.error;

public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(Long wordId) {
        super(String.format("Not found EkilexWord with word id: %d", wordId));
    }
}
