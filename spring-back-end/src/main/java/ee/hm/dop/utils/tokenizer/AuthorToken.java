package ee.hm.dop.utils.tokenizer;

public class AuthorToken extends DOPToken {

    public AuthorToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "author:\"" + getEscapedContent() + "\"";
    }
}
