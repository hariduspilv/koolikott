package ee.hm.dop.tokenizer;

public class OrToken extends DOPToken {

    public OrToken() {
        super("OR");
    }

    @Override
    public String toString() {
        return getContent();
    }
}
