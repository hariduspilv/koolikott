package ee.hm.dop.utils.tokenizer;

public class TagToken extends DOPToken {


    public TagToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "tag:\"" + getEscapedContent() + "\"";
    }


}
