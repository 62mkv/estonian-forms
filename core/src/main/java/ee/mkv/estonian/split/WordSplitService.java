package ee.mkv.estonian.split;

import ee.mkv.estonian.split.domain.Splitting;
import ee.mkv.estonian.split.domain.WordComponent;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WordSplitService {

    private static final int MIN_COMPONENT_LENGTH = 2;

    private static Set<Splitting> classicSplitting(String word) {
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

    /**
     * Will find all possible (2-component max) splittings where any component is longer than MIN_COMPONENT_LENGTH
     *
     * @param word word to split
     * @return set of splittings (can be empty)
     */
    public Set<Splitting> findAllSplittings(String word) {
        if (word.contains("-")) {
            return splitWithHyphen(word);
        }
        return classicSplitting(word);
    }

    private Set<Splitting> splitWithHyphen(String word) {
        final char separator = '-';
        boolean onSeparator = false;
        List<WordComponent> candidates = new ArrayList<>();
        int lastIndex = 0;
        int componentIndex = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == separator) {
                if (!onSeparator) {
                    final WordComponent component = new WordComponent(lastIndex, componentIndex, word.substring(lastIndex, i));
                    componentIndex++;
                    candidates.add(component);
                    onSeparator = true;
                }
            } else if (onSeparator) {
                lastIndex = i;
                onSeparator = false;
            }
        }
        if (lastIndex < word.length()) {
            candidates.add(new WordComponent(lastIndex, componentIndex, word.substring(lastIndex)));
        }

        return Set.of(Splitting.hyphenated(candidates));
    }

}
