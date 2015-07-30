package ee.hm.dop.tokenizer;

public class RegularToken extends DOPToken {

    public RegularToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return getContentWithEscapedChars();
    }
}
