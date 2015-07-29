package ee.hm.dop.utils.tokens;

public class RegularToken extends DOPToken {

    public RegularToken(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return escapeQueryCharacters(getContent());
    }

    private String escapeQueryCharacters(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^'
                    || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?'
                    || c == '|' || c == '&' || c == ';' || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
