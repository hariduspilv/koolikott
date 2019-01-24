package ee.hm.dop.utils.tokenizer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class SummaryTokenTest {

    private static final String DOUBLE_QUOTE = "\"";

    @Test
    public void testToString() {
        SummaryToken token = new SummaryToken("Something here");
        assertEquals("summary:" + DOUBLE_QUOTE + "Something\\ here" + DOUBLE_QUOTE, token.toString());

        token = new SummaryToken("Many words: in description!");
        assertEquals("summary:" + DOUBLE_QUOTE + "Many\\ words\\:\\ in\\ description\\!" + DOUBLE_QUOTE,
                token.toString());
    }
}
