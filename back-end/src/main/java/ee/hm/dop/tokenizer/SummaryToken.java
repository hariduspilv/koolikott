package ee.hm.dop.tokenizer;

public class SummaryToken extends DOPToken {

    public SummaryToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "summary:\"" + getEscapedContent() + "\"";
    }
}
