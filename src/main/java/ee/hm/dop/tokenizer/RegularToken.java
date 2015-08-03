package ee.hm.dop.tokenizer;

public class RegularToken extends DOPToken {

    public RegularToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        String escapedContent = getEscapedContent();
        return getContent().length() > 3 ? escapedContent + "*" : escapedContent;
    }
}
