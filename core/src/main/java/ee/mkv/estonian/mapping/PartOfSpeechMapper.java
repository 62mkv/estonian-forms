package ee.mkv.estonian.mapping;

import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.model.InternalPartOfSpeech;

public class PartOfSpeechMapper {

    private PartOfSpeechMapper() {
    }

    public static InternalPartOfSpeech fromEkiPartOfSpeech(EkiPartOfSpeech ekiPartOfSpeech) {
        return switch (ekiPartOfSpeech) {
            case ADJECTIVE -> InternalPartOfSpeech.ADJECTIVE;
            case ADVERB -> InternalPartOfSpeech.ADVERB;
            case CONJUNCTION -> InternalPartOfSpeech.CONJUNCTION;
            case INTERJECTION -> InternalPartOfSpeech.INTERJECTION;
            case NOUN -> InternalPartOfSpeech.NOUN;
            case NUMERAL -> InternalPartOfSpeech.NUMERAL;
            case POSTPOSITION -> InternalPartOfSpeech.POSTPOSITION;
            case PREPOSITION -> InternalPartOfSpeech.PREPOSITION;
            case PRONOUN -> InternalPartOfSpeech.PRONOUN;
            case VERB -> InternalPartOfSpeech.VERB;
            case PREFIX -> InternalPartOfSpeech.PREFIX;
        };
    }

    public static InternalPartOfSpeech fromPartOfSpeech(PartOfSpeech partOfSpeech) {
        return InternalPartOfSpeech.fromEkiCodes(partOfSpeech.getEkiCodes());
    }
}
