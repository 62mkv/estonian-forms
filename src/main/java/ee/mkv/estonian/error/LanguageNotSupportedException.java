package ee.mkv.estonian.error;

public class LanguageNotSupportedException extends RuntimeException {
    public LanguageNotSupportedException(String lang) {
        super(String.format("Language [] is not supported!", lang));
    }
}
