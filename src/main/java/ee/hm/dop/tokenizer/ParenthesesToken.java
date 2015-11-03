package ee.hm.dop.tokenizer;

import java.util.List;

public class ParenthesesToken extends DOPToken {

    private static final char OPENING_PARENTHESES = '(';
    private static final char CLOSING_PARENTHESES = ')';

    private List<DOPToken> tokens;

    public ParenthesesToken(List<DOPToken> tokens) {
        super(null);
        this.tokens = tokens;
    }

    @Override
    public String toString() {
        if (tokens != null && !tokens.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (DOPToken token : tokens) {
                sb.append(token + " ");
            }
            return OPENING_PARENTHESES + sb.toString().trim() + CLOSING_PARENTHESES;
        }
        return "\\" + OPENING_PARENTHESES + "\\" + CLOSING_PARENTHESES;
    }
}
