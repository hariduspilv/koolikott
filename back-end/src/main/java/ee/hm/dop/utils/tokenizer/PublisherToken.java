package ee.hm.dop.utils.tokenizer;

public class PublisherToken extends DOPToken {

    public PublisherToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "publisher:\"" + getEscapedContent() + "\"";
    }
}
