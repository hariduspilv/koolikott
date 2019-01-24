package ee.hm.dop.utils.tokenizer;

public class DescriptionToken extends DOPToken {

    public DescriptionToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "description:\"" + getEscapedContent() + "\"";
    }
}
