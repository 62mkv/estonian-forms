package ee.mkv.estonian.utils;

import org.junit.Test;

import java.util.Collection;

import static ee.mkv.estonian.utils.StringUtils.adaptBaseForm;
import static ee.mkv.estonian.utils.StringUtils.splitFormCode;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class StringUtilsTest {

    @Test
    public void splitFormCodeTest1() {
        String s = "PtsPrPs_";
        Collection<String> result = splitFormCode(s);
        assertThat(result, hasItems("Pts", "Pr", "Ps", "_"));
    }

    @Test
    public void splitFormCodeTest2() {
        String s = "IndPrSg1";
        Collection<String> result = splitFormCode(s);
        assertThat(result, hasItems("Ind", "Pr", "Sg", "1"));
    }

    @Test
    public void splitFormCodeTest3() {
        String s = "ID";
        Collection<String> result = splitFormCode(s);
        assertThat(result, hasItems("ID"));
    }

    @Test
    public void adaptBaseFormTestReplace() {
        String result = adaptBaseForm("aja+loolis-geograafilistele");
        assertThat(result, equalTo("ajaloolis-geograafilistele"));

        String result2 = adaptBaseForm("vil!");
        assertThat(result2, equalTo("vil"));
    }
}
