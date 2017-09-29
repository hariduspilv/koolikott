package ee.hm.dop.utils.tokenizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PublisherTokenTest {

    private static final String DOUBLE_QUOTE = "\"";

    @Test
    public void testToString() {
        PublisherToken token = new PublisherToken("Cool publisher");
        assertEquals("publisher:" + DOUBLE_QUOTE + "Cool\\ publisher" + DOUBLE_QUOTE, token.toString());

        token = new PublisherToken("!&Such cool publisher");
        assertEquals("publisher:" + DOUBLE_QUOTE + "\\!\\&Such\\ cool\\ publisher" + DOUBLE_QUOTE, token.toString());
    }
}
