package ee.hm.dop.utils.tokenizer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AuthorTokenTest {

    private static final String DOUBLE_QUOTE = "\"";

    @Test
    public void testToString() {
        AuthorToken token = new AuthorToken("Isaac John Newton");
        assertEquals("author:" + DOUBLE_QUOTE + "Isaac\\ John\\ Newton" + DOUBLE_QUOTE, token.toString());

        token = new AuthorToken("Isaac John Newton!&");
        assertEquals("author:" + DOUBLE_QUOTE + "Isaac\\ John\\ Newton\\!\\&" + DOUBLE_QUOTE, token.toString());
    }
}
