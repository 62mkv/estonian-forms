package ee.mkv.estonian.utils;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static ee.mkv.estonian.utils.StringUtils.*;
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

    @Test
    public void testTails() {
        Set<String> actual = StringUtils.getTails("word", 1);
        assertThat(actual, Matchers.containsInAnyOrder("ord", "rd", "d"));
        assertThat(actual, Matchers.hasSize(3));

        Set<String> actual2 = StringUtils.getTails("würd", 2);
        assertThat(actual2, Matchers.containsInAnyOrder("ürd", "rd"));
        assertThat(actual2, Matchers.hasSize(2));

        Set<String> actual3 = StringUtils.getTails("word", 3);
        assertThat(actual3, Matchers.containsInAnyOrder("ord"));
        assertThat(actual3, Matchers.hasSize(1));

        Set<String> actual4 = StringUtils.getTails("word", 4);
        assertThat(actual4, Matchers.empty());
    }

    @Test
    public void testHeads() {
        assertThat(getHeadForTail("word", "ord"), Matchers.is("w"));
        assertThat(getHeadForTail("agrotoostkoondis", "koondis"), Matchers.is("agrotoost"));
    }
}
