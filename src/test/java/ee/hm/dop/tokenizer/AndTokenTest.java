package ee.hm.dop.tokenizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AndTokenTest {

    @Test
    public void testToString() {
        AndToken token = new AndToken();
        assertEquals("AND", token.toString());
    }
}
