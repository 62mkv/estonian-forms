package ee.mkv.estonian.model;

import lombok.Value;

@Value
public class SplitCandidate {
    private String component;
    private int startsAt;
}
