package ee.mkv.estonian.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum PartOfSpeechEnum {
    ADJECTIVE("Adjective", "AH", "adj"), // adjektiiv, omadussõna
    ADVERB("Adverb", "D", "adv"), // adverb, määrsõna
    CONJUNCTION("Conjunction", "J", "konj"), // konjunktsioon, sidesõna
    INTERJECTION("Interjection", "I", "interj"), // interjektsioon, hüüdsõna
    NOUN("Noun", "GS", "s"), // substantiiv, nimisõna
    NUMERAL("Numeral", "ON", "num"), // numeraal, arvsõna
    POSTPOSITION("Postposition", "K", "postp"), // postpositsioon, tagasõna
    PREPOSITION("Preposition", "K", "prep"), // prepositsioon, eessõna
    PRONOUN("Pronoun", "P", "pron"), // pronoomen, asesõna
    VERB("Verb", "XV", "v"); // verb, tegusõna

    private final String representation;
    private final String ekiCodes;
    private final String ekilexCode;

    PartOfSpeechEnum(String representation, String ekiCodes, String ekilexCode) {
        this.representation = representation;
        this.ekiCodes = ekiCodes;
        this.ekilexCode = ekilexCode;
    }

    public static Optional<PartOfSpeechEnum> fromEkilexCode(String ekilexCode) {
        for (PartOfSpeechEnum value : PartOfSpeechEnum.values()) {
            if (value.ekilexCode.equalsIgnoreCase(ekilexCode)) {
                return Optional.of(value);
            }
        }

        return fromHackedEkilexCode(ekilexCode);
    }

    private static Optional<PartOfSpeechEnum> fromHackedEkilexCode(String ekilexCode) {
        switch (ekilexCode) {
            case "prop":
                return Optional.of(NOUN); // prooprium, pärisnimi
            default:
                return Optional.empty();
        }
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
