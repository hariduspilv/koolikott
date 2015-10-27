package ee.hm.dop.tokenizer;

public class SyntaxToken extends DOPToken {

    public SyntaxToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return getContent();
    }
}
