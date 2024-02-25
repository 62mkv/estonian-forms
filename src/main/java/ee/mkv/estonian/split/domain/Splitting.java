package ee.mkv.estonian.split.domain;

import lombok.Value;

import java.util.Comparator;
import java.util.List;

@Value
public class Splitting {
    List<WordComponent> components;

    public WordComponent findLastComponent() {
        return components.stream()
                .max(Comparator.comparing(WordComponent::getPosition))
                .orElseThrow(() -> new IllegalStateException("No components found in splitting"));
    }
}
