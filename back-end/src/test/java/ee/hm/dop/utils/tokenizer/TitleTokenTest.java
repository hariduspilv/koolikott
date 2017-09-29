package ee.hm.dop.utils.tokenizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TitleTokenTest {

    private static final String DOUBLE_QUOTE = "\"";

    @Test
    public void testToString() {
        TitleToken token = new TitleToken("The Capital");
        assertEquals("title:" + DOUBLE_QUOTE + "The\\ Capital" + DOUBLE_QUOTE, token.toString());

        token = new TitleToken("A Test Title!?");
        assertEquals("title:" + DOUBLE_QUOTE + "A\\ Test\\ Title\\!\\?" + DOUBLE_QUOTE, token.toString());
    }
}
