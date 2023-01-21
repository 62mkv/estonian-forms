package ee.mkv.estonian.error;

public class FormTypeCombinationNotFound extends RuntimeException {
    public FormTypeCombinationNotFound(String code) {
        super(String.format("FormTypeCombination for code [%s] not found!", code));
    }
}
