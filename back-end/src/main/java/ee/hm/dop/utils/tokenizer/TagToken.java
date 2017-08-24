package ee.hm.dop.utils.tokenizer;

/**
 * Created by mart on 5.02.16.
 */
public class TagToken extends DOPToken {


    public TagToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return "tag:\"" + getEscapedContent() + "\"";
    }


}
