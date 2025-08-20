package ee.mkv.estonian.ekilex.error;

public class RepresentationNotAllowedException extends RuntimeException {
    public RepresentationNotAllowedException(String wordValue) {
        super("Representation with value '" + wordValue + "' is not allowed. ");
    }
}
