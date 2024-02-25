package ee.mkv.estonian.split;

import ee.mkv.estonian.split.domain.Splitting;
import ee.mkv.estonian.split.domain.WordComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WordSplitServiceTest {

    private WordSplitService subject = new WordSplitService();

    @ParameterizedTest
    @ValueSource(strings = {"a", "aa", "aaa", "aaaa", "aaaaa"})
    void testShortWords(String source) {
        assertThat(subject.findAllSplittings(source)).containsExactly(lonelySplitting(source));
    }

    @Test
    void testOneLevelSplitting() {
        assertThat(subject.findAllSplittings("123456789")).containsExactlyInAnyOrder(
                new Splitting(List.of(
                        new WordComponent(0, 0, "123"),
                        new WordComponent(3, 1, "456789")
                )),
                new Splitting(List.of(
                        new WordComponent(0, 0, "1234"),
                        new WordComponent(4, 1, "56789")
                )),
                new Splitting(List.of(
                        new WordComponent(0, 0, "12345"),
                        new WordComponent(5, 1, "6789")
                )),
                new Splitting(List.of(
                        new WordComponent(0, 0, "123456"),
                        new WordComponent(6, 1, "789")
                ))
        );
    }

    private Splitting lonelySplitting(String source) {
        return new Splitting(List.of(new WordComponent(0, 0, source)));
    }

}