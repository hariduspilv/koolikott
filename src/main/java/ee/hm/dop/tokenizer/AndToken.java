package ee.hm.dop.tokenizer;

public class AndToken extends DOPToken {

    public AndToken() {
        super("AND");
    }

    @Override
    public String toString() {
        return getContent();
    }
}
