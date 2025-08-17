package ee.mkv.estonian.ekilex.error;

public class EkilexParadigmExistsException extends RuntimeException {
    public EkilexParadigmExistsException(Long wordId) {
        super("Paradigm for word with id " + wordId + " already exists in EkiLex");
    }
}
