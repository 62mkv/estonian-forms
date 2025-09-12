package ee.mkv.estonian.ekilex.error;

public class UnsupportedEkilexWordException extends RuntimeException {
    public UnsupportedEkilexWordException(String wordValue) {
        super("Unsupported Ekilex word: " + wordValue);
    }
}
