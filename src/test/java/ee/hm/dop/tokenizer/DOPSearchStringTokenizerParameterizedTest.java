package ee.hm.dop.tokenizer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DOPSearchStringTokenizerParameterizedTest {

    private String tokenKeyword;

    public DOPSearchStringTokenizerParameterizedTest(String tokenKeyword) {
        this.tokenKeyword = tokenKeyword;
    }

    @Parameters
    public static Collection<Object[]> getTokenKeywords() {
        return Arrays.asList(
                new Object[][] { { "author" }, { "title" }, { "description" }, { "summary" }, { "publisher" } });
    }

    @Test
    public void tokenize() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(tokenKeyword + ":Fibonacci");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals(tokenKeyword + ":\"fibonacci\"", searchQuery);
    }

    @Test
    public void tokenizeExactMatch() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(tokenKeyword + ":\"Leonardo Fibonacci\"");
        String searchQuery = consumeTokenizer(tokenizer);
        assertEquals(tokenKeyword + ":\"leonardo\\ fibonacci\"", searchQuery);
    }

    @Test
    public void tokenizeExactMatchMissingClosingQuotes() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(tokenKeyword + ":\"Leonardo Fibonacci");
        String searchQuery = consumeTokenizer(tokenizer);
        assertEquals(tokenKeyword + ":\"leonardo\" fibonacci*", searchQuery);
    }

    @Test
    public void tokenizeMissingColon() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(tokenKeyword + "\"Leonardo Fibonacci\"");
        String searchQuery = consumeTokenizer(tokenizer);
        assertEquals(tokenKeyword + "\\\"leonardo* fibonacci\\\"*", searchQuery);
    }

    @Test
    public void tokenizeSpaceInsteadColon() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(tokenKeyword + " \"Leonardo Fibonacci");
        String searchQuery = consumeTokenizer(tokenizer);
        if (tokenKeyword.length() > 3) {
            assertEquals(tokenKeyword + "* \\\"leonardo* fibonacci*", searchQuery);
        } else {
            assertEquals(tokenKeyword + " \\\"leonardo* fibonacci*", searchQuery);
        }
    }

    @Test
    public void tokenizeUppercase() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(tokenKeyword.toUpperCase() + ":Fibonacci");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals(tokenKeyword + ":\"fibonacci\"", searchQuery);
    }

    @Test
    public void tokenizeLastWord() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("math " + tokenKeyword + ":Fibonacci");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("math* " + tokenKeyword + ":\"fibonacci\"", searchQuery);
    }

    @Test
    public void tokenizeLastWordWithQuotes() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer("math " + tokenKeyword + ":\"Fibonacci\"");
        String searchQuery = consumeTokenizer(tokenizer);

        assertEquals("math* " + tokenKeyword + ":\"fibonacci\"", searchQuery);
    }

    @Test
    public void tokenizeOnlyTokenKeyword() {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(tokenKeyword + " rest");
        String searchQuery = consumeTokenizer(tokenizer);

        if (tokenKeyword.length() > 3) {
            assertEquals(tokenKeyword + "* rest*", searchQuery);
        } else {
            assertEquals(tokenKeyword + " rest*", searchQuery);
        }
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

}
