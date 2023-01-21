package ee.mkv.estonian.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
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
}
