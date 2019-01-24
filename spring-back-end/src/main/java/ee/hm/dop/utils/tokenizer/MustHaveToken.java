package ee.hm.dop.utils.tokenizer;

public class MustHaveToken extends DOPToken {

    private static final String PLUS = "+";
    private DOPToken content;

    public MustHaveToken(DOPToken content) {
        super(null);

        this.content = content;
    }

    @Override
    public String toString() {
        return content != null ? PLUS + content : PLUS;
    }
}
