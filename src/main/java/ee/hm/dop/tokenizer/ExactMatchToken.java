package ee.hm.dop.tokenizer;

public class ExactMatchToken extends DOPToken {

    public ExactMatchToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "\"" + getEscapedContent() + "\"";
    }
}
