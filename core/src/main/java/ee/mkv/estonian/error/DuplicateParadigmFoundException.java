package ee.mkv.estonian.error;

import java.util.HashSet;

public class DuplicateParadigmFoundException extends RuntimeException {
    public DuplicateParadigmFoundException(HashSet<Long> paradigms) {
        super("Duplicate paradigms found: " + paradigms);
    }
}
