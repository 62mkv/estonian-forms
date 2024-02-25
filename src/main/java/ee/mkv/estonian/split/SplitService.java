package ee.mkv.estonian.split;


import ee.mkv.estonian.model.SplitCandidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@Deprecated
public class SplitService {
    private static final int MIN_FIRST_COMPONENT = 4;
    private static final int MIN_LAST_COMPONENT = 4;

    public List<List<SplitCandidate>> splitByHyphen(String lemma) {
        final char separator = '-';
        boolean onSeparator = false;
        List<SplitCandidate> candidates = new ArrayList<>();
        int lastIndex = 0;
        for (int i = 0; i < lemma.length(); i++) {
            if (lemma.charAt(i) == separator) {
                if (!onSeparator) {
                    final SplitCandidate splitCandidate = new SplitCandidate(lemma.substring(lastIndex, i), lastIndex);
                    candidates.add(splitCandidate);
                    onSeparator = true;
                }
            } else if (onSeparator) {
                lastIndex = i;
                onSeparator = false;
            }
        }
        if (lastIndex < lemma.length()) {
            candidates.add(new SplitCandidate(lemma.substring(lastIndex), lastIndex));
        }

        return List.of(candidates);
    }

    public List<List<SplitCandidate>> splitNoHyphen(String lemma) {
        if (lemma.length() < MIN_LAST_COMPONENT + MIN_FIRST_COMPONENT) {
            return Collections.emptyList();
        }

        var result = new ArrayList<List<SplitCandidate>>();
        var len = lemma.length();
        for (int i = MIN_FIRST_COMPONENT; i <= len - MIN_LAST_COMPONENT; i++) {
            result.add(List.of(
                    new SplitCandidate(lemma.substring(0, i), 0),
                    new SplitCandidate(lemma.substring(i, lemma.length()), i)
            ));
        }
        return result;
    }

}
