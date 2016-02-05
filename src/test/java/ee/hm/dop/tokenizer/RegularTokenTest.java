package ee.hm.dop.tokenizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RegularTokenTest {

    @Test
    public void testToString() {
        RegularToken token = new RegularToken("test");
        assertEquals("test", token.toString());

        token = new RegularToken("\"test123/!");
        assertEquals("\\\"test123\\/\\!", token.toString());
    }
}
