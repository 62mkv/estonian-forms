package ee.mkv.estonian.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FormTypeCombinationEnum {
    SINGULAR_NOMINATIVE(1, "SgN", GrammaticNumber.SINGULAR, GrammaticCase.NOMINATIVE), //nimetav
    SINGULAR_GENITIVE(2, "SgG", GrammaticNumber.SINGULAR, GrammaticCase.GENITIVE), //omastav
    SINGULAR_PARTITIVE(3, "SgP", GrammaticNumber.SINGULAR, GrammaticCase.PARTITIVE), //osastav
    SINGULAR_ADITIVE(30, "SgAdt", GrammaticNumber.SINGULAR, GrammaticCase.ADITIVE), //short illative = lühike sisseütlev = suunduv
    SINGULAR_ILLATIVE(4, "SgIll", GrammaticNumber.SINGULAR, GrammaticCase.ILLATIVE), //sisseütlev
    SINGULAR_INESSIVE(5, "SgIn", GrammaticNumber.SINGULAR, GrammaticCase.INESSIVE), //seesütlev
    SINGULAR_ELATIVE(6, "SgEl", GrammaticNumber.SINGULAR, GrammaticCase.ELATIVE), //seestütlev
    SINGULAR_ALLATIVE(7, "SgAll", GrammaticNumber.SINGULAR, GrammaticCase.ALLATIVE), //alaleütlev
    SINGULAR_ADESSIVE(8, "SgAd", GrammaticNumber.SINGULAR, GrammaticCase.ADESSIVE), //alalütlev
    SINGULAR_ABLATIVE(9, "SgAbl", GrammaticNumber.SINGULAR, GrammaticCase.ABLATIVE), //alaltütlev
    SINGULAR_TRANSLATIVE(10, "SgTr", GrammaticNumber.SINGULAR, GrammaticCase.TRANSLATIVE), //saav
    SINGULAR_TERMINATIVE(11, "SgTer", GrammaticNumber.SINGULAR, GrammaticCase.TERMINATIVE), //rajav
    SINGULAR_ESSIVE(12, "SgEs", GrammaticNumber.SINGULAR, GrammaticCase.ESSIVE), //olev
    SINGULAR_ABESSIVE(13, "SgAb", GrammaticNumber.SINGULAR, GrammaticCase.ABESSIVE), //ilmaütlev
    SINGULAR_COMITATIVE(14, "SgKom", GrammaticNumber.SINGULAR, GrammaticCase.COMITATIVE), //kaasaütlev
    SINGULAR_GENITIVE_REDUCED(83, "RSgG", GrammaticNumber.SINGULAR, GrammaticCase.GENITIVE_REDUCED),
    PLURAL_NOMINATIVE(15, "PlN", GrammaticNumber.PLURAL, GrammaticCase.NOMINATIVE), //nimetav
    PLURAL_GENITIVE(16, "PlG", GrammaticNumber.PLURAL, GrammaticCase.GENITIVE),
    PLURAL_PARTITIVE(17, "PlP", GrammaticNumber.PLURAL, GrammaticCase.PARTITIVE),
    PLURAL_ILLATIVE(18, "PlIll", GrammaticNumber.PLURAL, GrammaticCase.ILLATIVE),
    PLURAL_INESSIVE(19, "PlIn", GrammaticNumber.PLURAL, GrammaticCase.INESSIVE),
    PLURAL_ELATIVE(20, "PlEl", GrammaticNumber.PLURAL, GrammaticCase.ELATIVE),
    PLURAL_ALLATIVE(21, "PlAll", GrammaticNumber.PLURAL, GrammaticCase.ALLATIVE),
    PLURAL_ADESSIVE(22, "PlAd", GrammaticNumber.PLURAL, GrammaticCase.ADESSIVE),
    PLURAL_ABLATIVE(23, "PlAbl", GrammaticNumber.PLURAL, GrammaticCase.ABLATIVE),
    PLURAL_TRANSLATIVE(24, "PlTr", GrammaticNumber.PLURAL, GrammaticCase.TRANSLATIVE),
    PLURAL_TERMINATIVE(25, "PlTer", GrammaticNumber.PLURAL, GrammaticCase.TERMINATIVE),
    PLURAL_ESSIVE(26, "PlEs", GrammaticNumber.PLURAL, GrammaticCase.ESSIVE),
    PLURAL_ABESSIVE(27, "PlAb", GrammaticNumber.PLURAL, GrammaticCase.ABESSIVE),
    PLURAL_COMITATIVE(28, "PlKom", GrammaticNumber.PLURAL, GrammaticCase.COMITATIVE),
    ROOT_PLURAL(29, "Rpl", GrammaticNumber.PLURAL, GrammaticCase.ROOT);
    private final int id;
    private final String ekiRepresentation;
    private final GrammaticNumber grammaticNumber;
    private final GrammaticCase grammaticCase;
}
