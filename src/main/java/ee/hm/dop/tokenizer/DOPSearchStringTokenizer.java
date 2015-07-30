package ee.hm.dop.tokenizer;

public class DOPSearchStringTokenizer {

    private String source;

    public DOPSearchStringTokenizer(String source) {
        this.source = source.trim();
    }

    public boolean hasMoreTokens() {
        return !source.isEmpty();
    }

    public DOPToken nextToken() {
        return parseStart(new StringBuilder());
    }

    private DOPToken parseStart(StringBuilder result) {
        Character c = getAndRemoveFirstCharacter();

        if (c.charValue() == '"' && source.indexOf('"') != -1) {
            return parseExactMatch(result);
        } else {
            return parseRegular(result.append(c));
        }
    }

    private DOPToken parseRegular(StringBuilder result) {
        Character c = getAndRemoveFirstCharacter();

        if (c == null || Character.isWhitespace(c.charValue())) {
            return createRegularToken(result);
        } else {
            return parseRegular(result.append(c));
        }
    }

    private DOPToken parseExactMatch(StringBuilder result) {
        Character c = getAndRemoveFirstCharacter();

        if (c.charValue() == '"') {
            return createExactMatchToken(result);
        } else {
            return parseExactMatch(result.append(c));
        }
    }

    private DOPToken createRegularToken(StringBuilder result) {
        source = source.trim();
        return new RegularToken(result.toString());
    }

    private DOPToken createExactMatchToken(StringBuilder result) {
        source = source.trim();
        return new ExactMatchToken(result.toString());
    }

    private Character getAndRemoveFirstCharacter() {
        Character c = null;
        if (!source.isEmpty()) {
            c = new Character(source.charAt(0));
            source = source.substring(1);
        }
        return c;
    }
}
