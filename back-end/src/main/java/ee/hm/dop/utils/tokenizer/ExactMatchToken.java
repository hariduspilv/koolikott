package ee.hm.dop.utils.tokenizer;

public class ExactMatchToken extends DOPToken {

    public ExactMatchToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        String escapedContent = getEscapedContent();
        return escapedContent.length() > 0 ? "\"" + escapedContent + "\"" : "";
    }
}
