package ee.hm.dop.tokenizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SyntaxTokenTest {

    @Test
    public void testToString() {
        SyntaxToken token = new SyntaxToken("AND");
        assertEquals("AND", token.toString());

        token = new SyntaxToken("longsyntaxkeyword");
        assertEquals("longsyntaxkeyword", token.toString());
    }
}
