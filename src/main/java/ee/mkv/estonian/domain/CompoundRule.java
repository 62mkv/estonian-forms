package ee.mkv.estonian.domain;

import lombok.Getter;

public enum CompoundRule {
    COMPOUND_OF_TWO_FIRST_SINGLE_NOMINATIVE(1),
    COMPOUND_OF_TWO_FIRST_SINGLE_GENITIVE(2),
    COMPOUND_OF_TWO_FIRST_PLURAL_NOMINATIVE(3),
    COMPOUND_OF_TWO_FIRST_PLURAL_GENITIVE(4),
    COMPOUND_WORD_SEPARATED_BY_HYPHEN(5),
    COMPLEX_COMPOUND_WORD(6);

    @Getter
    private final int id;

    CompoundRule(int id) {
        this.id = id;
    }
}
