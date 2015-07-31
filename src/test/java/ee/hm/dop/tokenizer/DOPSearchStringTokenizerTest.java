package ee.hm.dop.tokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

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
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("hello world", searchQuery);
    }

    @Test
    public void tokenizeExactMatch() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\"");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("\"hello\\ world\"", searchQuery);
    }

    @Test
    public void tokenizeEvenQuotesAtTheEnd() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" aabc\"");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("\"hello\\ world\" aabc\\\"", searchQuery);
    }

    @Test
    public void tokenizeEvenQuotesAtStart() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" \"aabc");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("\"hello\\ world\" \\\"aabc", searchQuery);
    }

    @Test
    public void tokenizeEvenQuotesInTheMiddle() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" aa\"bc");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("\"hello\\ world\" aa\\\"bc", searchQuery);
    }

    @Test
    public void nextTokenWhithoutCallingHasNextToken() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" abc \"again\"");

        assertEquals("\"hello\\ world\"", tokenizer.nextToken().toString());
        assertEquals("abc", tokenizer.nextToken().toString());
        assertEquals("\"again\"", tokenizer.nextToken().toString());

        try {
            tokenizer.nextToken();
            fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // Everything fine
        }
    }

    @Test
    public void tokenizeUsingTabsAndNewLine() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\t\n\r\"hello\nworld\"\ra\tabc\"\t\n\r");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("\"hello\\\nworld\" a abc\\\"", searchQuery);
    }

    @Test
    public void emptyQuotes() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"\"");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("", searchQuery);
    }

    private String consumeTokenizer(DOPSearchStringTokenizer tokenizer) {
        StringBuilder searchQuery = new StringBuilder();

        while (tokenizer.hasMoreTokens()) {
            DOPToken token = tokenizer.nextToken();
            searchQuery.append(token);

            if (tokenizer.hasMoreTokens()) {
                searchQuery.append(" ");
            }
        }

        return searchQuery.toString();
    }

    @Test
    public void tokenizeSpecialCharacters() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("\"hello world\" aabc\" +-!");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("\"hello\\ world\" aabc\\\" \\+\\-\\!", searchQuery);
    }
}
