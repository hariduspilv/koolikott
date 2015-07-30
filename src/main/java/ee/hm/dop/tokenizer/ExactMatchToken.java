package ee.hm.dop.utils.tokens;

public class ExactMatchToken extends DOPToken {

    public ExactMatchToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "\"" + this.getContent() + "\"";
    }
}
