package ee.hm.dop.tokenizer;

public class PublisherToken extends DOPToken {

    public PublisherToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "publisher:\"" + getEscapedContent() + "\"";
    }
}
