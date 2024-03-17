package ee.mkv.estonian.split.domain;

import lombok.Value;

import java.util.Comparator;
import java.util.List;

@Value
public class Splitting {

    List<WordComponent> components;
    boolean isHyphenated;

    public Splitting(List<WordComponent> components, boolean isHyphenated) {
        this.isHyphenated = isHyphenated;
        this.components = components;
    }

    public Splitting(List<WordComponent> components) {
        this(components, false);
    }

    public WordComponent findLastComponent() {
        return components.stream()
                .max(Comparator.comparing(WordComponent::getPosition))
                .orElseThrow(() -> new IllegalStateException("No components found in splitting"));
    }

    public static Splitting hyphenated(List<WordComponent> components) {
        return new Splitting(components, true);
    }

    public Splitting upTo(WordComponent last) {
        return new Splitting(components.subList(0, components.indexOf(last) + 1), isHyphenated);
    }

    public WordComponent nextComponent(WordComponent component) {
        for (WordComponent wordComponent : components) {
            if (wordComponent.equals(component)) {
                return components.get(components.indexOf(wordComponent) + 1);
            }
        }
        return null;
    }
}
