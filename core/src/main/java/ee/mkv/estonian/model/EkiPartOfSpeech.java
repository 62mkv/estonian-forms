package ee.mkv.estonian.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum EkiPartOfSpeech {
    ADJECTIVE("Adjective", "AH", "adj"), // adjektiiv, omadussõna
    ADVERB("Adverb", "D", "adv"), // adverb, määrsõna
    CONJUNCTION("Conjunction", "J", "konj"), // konjunktsioon, sidesõna
    INTERJECTION("Interjection", "I", "interj"), // interjektsioon, hüüdsõna
    NOUN("Noun", "GS", "s"), // substantiiv, nimisõna
    NUMERAL("Numeral", "ON", "num"), // numeraal, arvsõna
    POSTPOSITION("Postposition", "K", "postp"), // postpositsioon, tagasõna
    PREPOSITION("Preposition", "K", "prep"), // prepositsioon, eessõna
    PRONOUN("Pronoun", "P", "pron"), // pronoomen, asesõna
    VERB("Verb", "XV", "v"),
    PREFIX("Prefix", "pf", "pf"); // verb, tegusõna

    private final String representation;
    private final String ekiCodes;
    private final String ekilexCode;

    EkiPartOfSpeech(String representation, String ekiCodes, String ekilexCode) {
        this.representation = representation;
        this.ekiCodes = ekiCodes;
        this.ekilexCode = ekilexCode;
    }

    public static Optional<EkiPartOfSpeech> fromEkilexCode(String ekilexCode) {
        for (EkiPartOfSpeech value : EkiPartOfSpeech.values()) {
            if (value.ekilexCode.equalsIgnoreCase(ekilexCode)) {
                return Optional.of(value);
            }
        }

        return fromHackedEkilexCode(ekilexCode);
    }

    private static Optional<EkiPartOfSpeech> fromHackedEkilexCode(String ekilexCode) {

        if (ekilexCode.equals("prop")) {
            return Optional.of(NOUN); // prooprium, pärisnimi
        }
        return Optional.empty();
    }

    public static Optional<EkiPartOfSpeech> fromEkiCodes(String ekiCode) {
        for (EkiPartOfSpeech value : EkiPartOfSpeech.values()) {
            if (value.ekiCodes.equalsIgnoreCase(ekiCode)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static Optional<EkiPartOfSpeech> fromRepresentation(String name) {
        for (EkiPartOfSpeech value : EkiPartOfSpeech.values()) {
            if (value.representation.equalsIgnoreCase(name)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static EkiPartOfSpeech from(int input) {
        for (EkiPartOfSpeech option : EkiPartOfSpeech.values()) {
            if (option.ordinal() + 1 == input) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid input");
    }
}
