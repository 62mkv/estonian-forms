package ee.mkv.estonian.error;

public class PartOfSpeechNotFoundException extends RuntimeException {
    public PartOfSpeechNotFoundException(String code) {
        super(String.format("Part of speech for code [%s] could not be found", code));
    }
}
