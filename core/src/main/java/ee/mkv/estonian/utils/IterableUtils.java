package ee.mkv.estonian.utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class IterableUtils {

    private IterableUtils() {
    }

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public static <T> T getUniqueValue(String value, List<T> values) {
        if (values.size() > 1) {
            throw new RuntimeException("More than one item found for " + value);
        }
        if (values.isEmpty()) {
            throw new RuntimeException("No item found for " + value);
        }
        return values.get(0);
    }

    public static <T> T getFirstValueOrFail(List<T> values) {
        if (values.isEmpty()) {
            throw new RuntimeException("No values found");
        }
        return values.get(0);
    }

    public static <T> Optional<T> getSingleValueIfExists(String value, List<T> values) {
        if (values.size() > 1) {
            throw new RuntimeException("More than one item found for " + value);
        }
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(values.get(0));
    }

}
