package ee.mkv.estonian.split.domain;

import lombok.Value;

@Value
public class WordComponent {
    /**
     * at which character index this component begins in a word
     */
    int startIndex;

    /**
     * which component it is in a word (0 = first, 1 = second, etc)
     */
    int position;

    /**
     * the component itself
     */
    String component;
}
