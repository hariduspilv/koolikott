package ee.hm.dop.tokenizer;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
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

        token.setContent("\"test&/())?");
        assertEquals(DOUBLE_QUOTE + "\\\"test\\&\\/\\(\\)\\)\\?" + DOUBLE_QUOTE, token.toString());

        char[] escapedChars = { '\\', '+', '-', '!', '(', ')', ':', '^', '[', ']', '\"', '{', '}', '~', '*', '?', '|',
                '&', ';', '/' };

        String testString = new String(escapedChars);
        token.setContent(testString);
        int count = StringUtils.countMatches(token.toString(), '\\');

        assertEquals(escapedChars.length + 1, count);
    }
}
