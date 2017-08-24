package ee.hm.dop.utils.tokenizer;

public class MustNotHaveToken extends DOPToken {

    private static final String MINUS = "-";
    private DOPToken content;

    public MustNotHaveToken(DOPToken content) {
        super(null);

        this.content = content;
    }

    @Override
    public String toString() {
        return content != null ? MINUS + content : MINUS;
    }
}
