package ee.mkv.estonian.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InternalPartOfSpeech {
    ADJECTIVE("AH", 1L),
    ADVERB("D", 2L),
    CONJUNCTION("J", 3L),
    INTERJECTION("I", 4L),
    NOUN("GS", 5L),
    NUMERAL("ON", 6L),
    POSTPOSITION("K", 7L),
    PREPOSITION("K", 8L),
    PRONOUN("P", 9L),
    VERB("XV", 10L),
    PREFIX("pf", 11L);

    @Getter
    private final String ekiCodes;

    @Getter
    private final Long id;

    public static InternalPartOfSpeech fromEkiCodes(String ekiCodes) {
        // find enum value by ekiCodes
        for (InternalPartOfSpeech value : InternalPartOfSpeech.values()) {
            if (value.ekiCodes.equalsIgnoreCase(ekiCodes)) {
                return value;
            }
        }

        return null;
    }

    public static InternalPartOfSpeech fromEkiPartOfSpeech(EkiPartOfSpeech ekiPartOfSpeech) {
        // find enum value by ekiPartOfSpeech
        for (InternalPartOfSpeech value : InternalPartOfSpeech.values()) {
            if (value.ekiCodes.equalsIgnoreCase(ekiPartOfSpeech.getEkiCodes())) {
                return value;
            }
        }

        return null;
    }

    public boolean isName() {
        return this == NOUN || this == ADJECTIVE;
    }

    public static InternalPartOfSpeech fromId(Long id) {
        if (id == null) {
            return null;
        }
        return switch (id.intValue()) {
            case 1 -> ADJECTIVE;
            case 2 -> ADVERB;
            case 3 -> CONJUNCTION;
            case 4 -> INTERJECTION;
            case 5 -> NOUN;
            case 6 -> NUMERAL;
            case 7 -> POSTPOSITION;
            case 8 -> PREPOSITION;
            case 9 -> PRONOUN;
            case 10 -> VERB;
            default -> null;
        };
    }
}
