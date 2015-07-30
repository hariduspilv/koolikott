package ee.hm.dop.tokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DOPSearchStringTokenizerTest {

    @Test
    public void tokenizeEmptyString() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("");
        assertFalse(tokenizer.hasMoreTokens());
    }

    @Test
    public void tokenizeWhitespaces() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("       ");
        assertFalse(tokenizer.hasMoreTokens());
    }

    @Test
    public void tokenizeNull() {
        try {
            new DOPSearchStringTokenizer(null);
            fail(); // Exception expected
        } catch (NullPointerException e) {
            // OK, ignore
        }
    }

    @Test
    public void tokenizeRegularQuery() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("hello world");
        StringBuilder sb = new StringBuilder();

        while (tokenizer.hasMoreTokens()) {
            DOPToken token = tokenizer.nextToken();
            sb.append(token);
            if (tokenizer.hasMoreTokens()) {
                sb.append(" ");
            }
        }
        assertEquals("hello world", sb.toString());
    }

    @Test
    public void tokenizeExactMatch() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\"");
        StringBuilder sb = new StringBuilder();

        while (tokenizer.hasMoreTokens()) {
            DOPToken token = tokenizer.nextToken();
            sb.append(token);
            if (tokenizer.hasMoreTokens()) {
                sb.append(" ");
            }
        }
        assertEquals("\"hello\\ world\"", sb.toString());
    }

    @Test
    public void tokenizeEvenQuotes() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" aabc\"");
        StringBuilder sb = new StringBuilder();

        while (tokenizer.hasMoreTokens()) {
            DOPToken token = tokenizer.nextToken();
            sb.append(token);
            if (tokenizer.hasMoreTokens()) {
                sb.append(" ");
            }
        }
        assertEquals("\"hello\\ world\" aabc\\\"", sb.toString());
    }

    @Test
    public void tokenizeSpecialCharacters() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" aabc\" +-!");
        StringBuilder sb = new StringBuilder();

        while (tokenizer.hasMoreTokens()) {
            DOPToken token = tokenizer.nextToken();
            sb.append(token);
            if (tokenizer.hasMoreTokens()) {
                sb.append(" ");
            }
        }
        assertEquals("\"hello\\ world\" aabc\\\" \\+\\-\\!", sb.toString());
    }
}
