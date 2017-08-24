package ee.hm.dop.utils.tokenizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by mart on 5.02.16.
 */
public class TagTokenTest {

    private static final String DOUBLE_QUOTE = "\"";

    @Test
    public void testToString() {
        TagToken token = new TagToken("matemaatika");
        assertEquals("tag:" + DOUBLE_QUOTE + "matemaatika" + DOUBLE_QUOTE, token.toString());

        token = new TagToken("Isaac John Newton!&");
        assertEquals("tag:" + DOUBLE_QUOTE + "Isaac\\ John\\ Newton\\!\\&" + DOUBLE_QUOTE, token.toString());
    }
}
