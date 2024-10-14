package ee.mkv.estonian.utils;

import java.util.HashSet;
import java.util.Set;

public class SetUtils {
    private SetUtils() {
    }

    public static <T> Set<T> combineSets(Set<T> set1, Set<T> set2) {
        Set result = new HashSet(set1);
        result.addAll(set2);
        return result;
    }
}
