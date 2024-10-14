package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.FormTypeCombinationEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MineParadigmRestorerTest {

    private final MineParadigmRestorer subject = new MineParadigmRestorer();

    @Test
    void restoreParadigm() {
        var result = subject.restoreParadigm("realiseerimine");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_NOMINATIVE)).containsExactlyInAnyOrder("realiseerimine");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_GENITIVE)).containsExactlyInAnyOrder("realiseerimise");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_PARTITIVE)).containsExactlyInAnyOrder("realiseerimist");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_ADITIVE)).containsExactlyInAnyOrder("realiseerimisse");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_ILLATIVE)).containsExactlyInAnyOrder("realiseerimisesse");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_INESSIVE)).containsExactlyInAnyOrder("realiseerimises");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_ELATIVE)).containsExactlyInAnyOrder("realiseerimisest");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_ALLATIVE)).containsExactlyInAnyOrder("realiseerimisele");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_ADESSIVE)).containsExactlyInAnyOrder("realiseerimisel");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_ABLATIVE)).containsExactlyInAnyOrder("realiseerimiselt");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_TRANSLATIVE)).containsExactlyInAnyOrder("realiseerimiseks");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_TERMINATIVE)).containsExactlyInAnyOrder("realiseerimiseni");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_ESSIVE)).containsExactlyInAnyOrder("realiseerimisena");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_ABESSIVE)).containsExactlyInAnyOrder("realiseerimiseta");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_COMITATIVE)).containsExactlyInAnyOrder("realiseerimisega");
        assertThat(result.get(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED)).containsExactlyInAnyOrder("realiseerimis");

        assertThat(result.get(FormTypeCombinationEnum.PLURAL_NOMINATIVE)).containsExactlyInAnyOrder("realiseerimised");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_GENITIVE)).containsExactlyInAnyOrder("realiseerimiste");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_PARTITIVE)).containsExactlyInAnyOrder("realiseerimisi");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_ILLATIVE)).containsExactlyInAnyOrder("realiseerimistesse", "realiseerimisisse");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_INESSIVE)).containsExactlyInAnyOrder("realiseerimistes", "realiseerimisis");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_ELATIVE)).containsExactlyInAnyOrder("realiseerimistest", "realiseerimisist");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_ALLATIVE)).containsExactlyInAnyOrder("realiseerimistele", "realiseerimisile");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_ADESSIVE)).containsExactlyInAnyOrder("realiseerimistel", "realiseerimisil");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_ABLATIVE)).containsExactlyInAnyOrder("realiseerimistelt", "realiseerimisilt");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_TRANSLATIVE)).containsExactlyInAnyOrder("realiseerimisteks", "realiseerimisiks");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_TERMINATIVE)).containsExactlyInAnyOrder("realiseerimisteni");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_ESSIVE)).containsExactlyInAnyOrder("realiseerimistena");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_ABESSIVE)).containsExactlyInAnyOrder("realiseerimisteta");
        assertThat(result.get(FormTypeCombinationEnum.PLURAL_COMITATIVE)).containsExactlyInAnyOrder("realiseerimistega");
        assertThat(result.get(FormTypeCombinationEnum.ROOT_PLURAL)).containsExactlyInAnyOrder("realiseerimisi");
        assertThat(result.keySet()).hasSize(31);
    }
}