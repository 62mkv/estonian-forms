package ee.mkv.estonian.error;

public class EkilexWordNotFoundException extends RuntimeException {
    public EkilexWordNotFoundException(Long wordId) {
        super(String.format("Not found EkilexWord with word id: %d", wordId));
    }
}
