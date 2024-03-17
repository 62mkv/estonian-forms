package ee.mkv.estonian.model;

import lombok.Getter;

public enum InternalPartOfSpeech {
    ADJECTIVE("AH"),
    ADVERB("D"),
    CONJUNCTION("J"),
    INTERJECTION("I"),
    NOUN("GS"),
    NUMERAL("ON"),
    POSTPOSITION("K"),
    PREPOSITION("K"),
    PRONOUN("P"),
    VERB("XV"),
    PREFIX("pf");

    @Getter
    private final String ekiCodes;

    InternalPartOfSpeech(String ekiCodes) {
        this.ekiCodes = ekiCodes;
    }

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
}
