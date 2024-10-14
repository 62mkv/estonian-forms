package ee.mkv.estonian.model;

public enum GrammaticCase {
    NOMINATIVE, //nimetav
    GENITIVE, //omastav
    PARTITIVE, //osastav
    ADITIVE, //short illative = lühike sisseütlev = suunduv
    ILLATIVE, //sisseütlev
    INESSIVE, //seesütlev
    ELATIVE, //seestütlev
    ALLATIVE, //alaleütlev
    ADESSIVE, //alalütlev
    ABLATIVE, //alaltütlev
    TRANSLATIVE, //saav
    TERMINATIVE, //rajav
    ESSIVE, //olev
    ABESSIVE, //ilmaütlev
    COMITATIVE, //kaasaütlev
    ROOT(true),
    GENITIVE_REDUCED(true);

    private final boolean isArtificial;

    GrammaticCase(boolean isArtificial) {
        this.isArtificial = isArtificial;
    }

    GrammaticCase() {
        this.isArtificial = false;
    }
}
