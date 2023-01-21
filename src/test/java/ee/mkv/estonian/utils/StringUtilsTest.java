package ee.mkv.estonian.utils;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Collection;

import static ee.mkv.estonian.utils.StringUtils.splitFormCode;
import static org.junit.Assert.assertThat;

public class StringUtilsTest {

    @Test
    public void splitFormCodeTest1() {
        String s = "PtsPrPs_";
        Collection<String> result = splitFormCode(s);
        assertThat(result, CoreMatchers.hasItems("Pts", "Pr", "Ps", "_"));
    }

    @Test
    public void splitFormCodeTest2() {
        String s = "IndPrSg1";
        Collection<String> result = splitFormCode(s);
        assertThat(result, CoreMatchers.hasItems("Ind", "Pr", "Sg", "1"));
    }

    @Test
    public void splitFormCodeTest3() {
        String s = "ID";
        Collection<String> result = splitFormCode(s);
        assertThat(result, CoreMatchers.hasItems("ID"));
    }
}
