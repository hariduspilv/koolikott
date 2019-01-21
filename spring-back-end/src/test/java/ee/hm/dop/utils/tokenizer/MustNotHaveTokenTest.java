package ee.hm.dop.utils.tokenizer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MustNotHaveTokenTest {

    @Test
    public void testToString() {
        RegularToken content = new RegularToken("test");
        MustNotHaveToken token = new MustNotHaveToken(content);
        assertEquals("-test", token.toString());

        AuthorToken authorToken = new AuthorToken("Leonardo Fibonacci");
        MustNotHaveToken mustNotHaveToken = new MustNotHaveToken(authorToken);
        assertEquals("-author:\"Leonardo\\ Fibonacci\"", mustNotHaveToken.toString());
    }

    @Test
    public void testNullContent() {
        RegularToken content = null;
        MustNotHaveToken token = new MustNotHaveToken(content);
        assertEquals("-", token.toString());
    }

}
