package ee.hm.dop.tokenizer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ParenthesesTokenTest {

    private static final char OPENING_PARENTHESES = '(';
    private static final char CLOSING_PARENTHESES = ')';

    @Test
    public void testToString() {
        RegularToken regularToken = new RegularToken("stuff");
        TitleToken titleToken = new TitleToken("Old Book");
        RegularToken regularToken2 = new RegularToken("more");
        List<DOPToken> tokens = Arrays.asList(regularToken, titleToken, regularToken2);

        ParenthesesToken token = new ParenthesesToken(tokens);
        assertEquals(OPENING_PARENTHESES + "stuff* title:\"Old\\ Book\" more*" + CLOSING_PARENTHESES, token.toString());
    }

    @Test
    public void parenthesesInParentheses() {
        RegularToken regularToken = new RegularToken("stuff");
        RegularToken regularToken2 = new RegularToken("more");
        List<DOPToken> tokens = Arrays.asList(regularToken, regularToken2);

        ParenthesesToken innerToken = new ParenthesesToken(tokens);
        assertEquals(OPENING_PARENTHESES + "stuff* more*" + CLOSING_PARENTHESES, innerToken.toString());

        RegularToken regularToken3 = new RegularToken("things");
        tokens = Arrays.asList(regularToken3, innerToken);

        ParenthesesToken outerToken = new ParenthesesToken(tokens);
        assertEquals(OPENING_PARENTHESES + "things* " + OPENING_PARENTHESES + "stuff* more*" + CLOSING_PARENTHESES
                + CLOSING_PARENTHESES, outerToken.toString());
    }

    @Test
    public void emptyToken() {
        List<DOPToken> tokens = new ArrayList<>();

        ParenthesesToken token = new ParenthesesToken(tokens);
        assertEquals("\\" + OPENING_PARENTHESES + "\\" + CLOSING_PARENTHESES, token.toString());
    }

    @Test
    public void nullTokens() {
        ParenthesesToken token = new ParenthesesToken(null);
        assertEquals("\\" + OPENING_PARENTHESES + "\\" + CLOSING_PARENTHESES, token.toString());
    }
}
