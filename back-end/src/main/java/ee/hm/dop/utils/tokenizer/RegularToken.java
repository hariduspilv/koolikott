package ee.hm.dop.utils.tokenizer;

public class RegularToken extends DOPToken {

    public RegularToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return getEscapedContent();
    }
}
