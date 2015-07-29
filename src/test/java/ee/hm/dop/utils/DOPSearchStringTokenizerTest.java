package ee.hm.dop.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DOPSearchStringTokenizerTest {

    @Test
    public void tokenizeEmptyString() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("");
        assertEquals("", tokenizer.getWhitespaceSeparatedTokens());
    }

    @Test
    public void tokenizeWhitespaces() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("       ");
        assertEquals("", tokenizer.getWhitespaceSeparatedTokens());
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
        assertEquals("hello world", tokenizer.getWhitespaceSeparatedTokens());
    }

    @Test
    public void tokenizeExactMatch() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\"");
        assertEquals("\"hello world\"", tokenizer.getWhitespaceSeparatedTokens());
    }

    @Test
    public void tokenizeEvenQuotes() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" aabc\"");
        assertEquals("\"hello world\" aabc\\\"", tokenizer.getWhitespaceSeparatedTokens());
    }

    @Test
    public void tokenizeSpecialCharacters() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" aabc\" +-!");
        assertEquals("\"hello world\" aabc\\\" \\+\\-\\!", tokenizer.getWhitespaceSeparatedTokens());
    }
}
