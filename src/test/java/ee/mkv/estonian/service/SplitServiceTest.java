package ee.mkv.estonian.service;


import ee.mkv.estonian.model.SplitCandidate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SplitServiceTest {

    private final SplitService subject = new SplitService();

    private static SplitCandidate sc(String lemm, int startsAt) {
        return new SplitCandidate(lemm, startsAt);
    }

    @Test
    void testSplitWithHyphen() {
        var lemma = "poo-ooh-kjalt";

        var result = subject.splitByHyphen(lemma);
        var sc1 = sc("poo", 0);
        var sc2 = sc("ooh", 4);
        var sc3 = sc("kjalt", 8);
        assertThat(result).containsExactly(List.of(sc1, sc2, sc3));
    }

    @Test
    void testSplitTooShort() {
        var lemma = "2short";
        var result = subject.splitNoHyphen(lemma);
        assertThat(result).isEmpty();
    }

    @Test
    void testSplit() {
        var lemma = "lemmikhooaeg";
        var result = subject.splitNoHyphen(lemma);

        assertThat(result).containsExactly(
                List.of(sc("lemm", 0), sc("ikhooaeg", 4)),
                List.of(sc("lemmi", 0), sc("khooaeg", 5)),
                List.of(sc("lemmik", 0), sc("hooaeg", 6)),
                List.of(sc("lemmikh", 0), sc("ooaeg", 7)),
                List.of(sc("lemmikho", 0), sc("oaeg", 8))
        );

    }

}