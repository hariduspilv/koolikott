package ee.hm.dop.utils.tokens;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExactMatchTokenTest {

    private static final String DOUBLE_QUOTE = "\"";

    @Test
    public void testToString() {
        ExactMatchToken token = new ExactMatchToken("test");
        assertEquals(DOUBLE_QUOTE + "test" + DOUBLE_QUOTE, token.toString());

        token.setContent("\"test&/())=?");
        assertEquals(DOUBLE_QUOTE + "\"test&/())=?" + DOUBLE_QUOTE, token.toString());
    }
}
