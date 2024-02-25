package ee.mkv.estonian.split;

import ee.mkv.estonian.split.domain.Splitting;
import ee.mkv.estonian.split.domain.WordComponent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class WordSplitService {

    private static final int MIN_COMPONENT_LENGTH = 3;

    /**
     * Will find all possible (2-component max) splittings where any component is longer than MIN_COMPONENT_LENGTH
     *
     * @param word
     * @return
     */
    public Set<Splitting> findAllSplittings(String word) {
        final int length = word.length();
        if (length < 2 * MIN_COMPONENT_LENGTH) {
            return Collections.singleton(new Splitting(List.of(new WordComponent(0, 0, word))));
        }
        Set<Splitting> result = new HashSet<>(length - MIN_COMPONENT_LENGTH);
        for (int i = MIN_COMPONENT_LENGTH; i <= length - MIN_COMPONENT_LENGTH; i++) {
            var component1 = new WordComponent(0, 0, word.substring(0, i));
            var component2 = new WordComponent(i, 1, word.substring(i, length));
            result.add(new Splitting(List.of(component1, component2)));
        }

        return result;
    }

}
