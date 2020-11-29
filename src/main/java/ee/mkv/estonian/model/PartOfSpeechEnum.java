package ee.mkv.estonian.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum PartOfSpeechEnum {
    ADJECTIVE("Adjective", "AH"),
    ADVERB("Adverb", "D"),
    CONJUNCTION("Conjunction", "J"),
    INTERJECTION("Interjection", "I"),
    NOUN("Noun", "GS"),
    NUMERAL("Numeral", "ON"),
    POSTPOSITION("Postposition", "K"),
    PREPOSITION("Preposition", "K"),
    PRONOUN("Pronoun", "P"),
    VERB("Verb", "XV");

    private final String representation;
    private final String ekiCodes;

    PartOfSpeechEnum(String representation, String ekiCodes) {
        this.representation = representation;
        this.ekiCodes = ekiCodes;
    }

    public static Optional<PartOfSpeechEnum> fromEkiCode(char ekiCode) {
        for (PartOfSpeechEnum value : PartOfSpeechEnum.values()) {
            if (value.ekiCodes.contains(String.valueOf(ekiCode))) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static Optional<PartOfSpeechEnum> fromRepresentation(String name) {
        for (PartOfSpeechEnum value : PartOfSpeechEnum.values()) {
            if (value.representation.equalsIgnoreCase(name)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
