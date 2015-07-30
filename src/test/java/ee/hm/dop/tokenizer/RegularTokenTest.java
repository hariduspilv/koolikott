package ee.hm.dop.utils.tokens;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RegularTokenTest {

    @Test
    public void testToString() {
        RegularToken token = new RegularToken("test");
        assertEquals("test", token.toString());

        token.setContent("\"test123/!");
        assertEquals("\\\"test123\\/\\!", token.toString());

        char[] escapedChars = { '\\', '+', '-', '!', '(', ')', ':', '^', '[', ']', '\"', '{', '}', '~', '*', '?', '|',
                '&', ';', '/' };

        String testString = new String(escapedChars);
        token.setContent(testString);
        int count = StringUtils.countMatches(token.toString(), '\\');

        assertEquals(escapedChars.length + 1, count);
    }
}
