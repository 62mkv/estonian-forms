package ee.mkv.estonian.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class StringUtils {
    private static final Pattern pattern = Pattern.compile("((\\p{Upper}[\\p{Lower}]*)|(\\d)|(_))");

    public static Collection<String> splitFormCode(String s) {
        if ("ID".contentEquals(s)) {
            return Collections.singletonList(s);
        }
        Matcher m = pattern.matcher(s);
        List<String> result = new ArrayList<>(3);
        while (m.find()) {
            result.add(m.group(1));
        }

        if (result.contains("I") || result.contains("D")) {
            log.info("Unusual: {}", s);
        }
        return result;
    }

    public static String adaptBaseForm(String form) {
        return form
                .replaceAll("\\+", "")
                .replaceAll("\\!", "");
    }

    public static Set<String> getTails(String word, int minimalLength) {
        Set<String> tails = new HashSet<>();
        for (int i = 1; i < word.length() - minimalLength + 1; i++) {
            tails.add(word.substring(i));
        }
        return tails;
    }

    public static String getHeadForTail(String word, String tail) {
        return word.substring(0, word.length() - tail.length());
    }

    public static boolean isEmpty(String word) {
        return word == null || word.isEmpty();
    }
}
