package ee.mkv.estonian.error;

public class NonSingularValueException extends RuntimeException {
    public NonSingularValueException(String objectType, String property, Object value) {
        super(String.format("Value of type '%s' has non-singular value %s of property '%s'",
                objectType, value.toString(), property));
    }
}
