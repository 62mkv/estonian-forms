package ee.mkv.estonian.error;

public class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
        super("Operation not implemented");
    }

    public NotImplementedException(String message) {
        super(message);
    }
}
