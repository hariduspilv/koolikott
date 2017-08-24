package ee.hm.dop.utils.tokenizer;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DOPTokenTest {

    @Test
    public void testGetEscapedContent() {

        DOPToken token = new DOPToken("test") {

            @Override
            public String toString() {
                return getEscapedContent();
            }
        };

        assertEquals("test", token.toString());

        token = new DOPToken("\"test123/!") {

            @Override
            public String toString() {
                return getEscapedContent();
            }
        };

        assertEquals("\\\"test123\\/\\!", token.toString());

        char[] escapedChars = { '\\', '+', '-', '!', '(', ')', ':', '^', '[', ']', '\"', '{', '}', '~', '*', '?', '|',
                '&', ';', '/' };

        String testString = new String(escapedChars);
        token = new DOPToken(testString) {

            @Override
            public String toString() {
                return getEscapedContent();
            }
        };

        int count = StringUtils.countMatches(token.toString(), '\\');

        assertEquals(escapedChars.length + 1, count);
    }
}
