package ee.hm.dop.tokenizer;

import java.util.NoSuchElementException;

public class DOPSearchStringTokenizer {

    private String source;
    private int currentPosition;
    private int newPosition;
    private int maxPosition;

    public DOPSearchStringTokenizer(String source) {
        this.source = source.trim();
        this.currentPosition = 0;
        this.maxPosition = this.source.length();
        this.newPosition = -1;
    }

    public boolean hasMoreTokens() {
        newPosition = skipWhiteSpaces(currentPosition);
        return newPosition < maxPosition;
    }

    public DOPToken nextToken() {
        currentPosition = newPosition >= 0 ? newPosition : skipWhiteSpaces(currentPosition);

        // Reset anyway
        newPosition = -1;

        if (currentPosition >= maxPosition) {
            throw new NoSuchElementException();
        }

        return parse();
    }

    private DOPToken parse() {
        DOPToken token = null;

        if (currentPosition < maxPosition) {
            char c = source.charAt(currentPosition);

            if (c == '"') {
                token = parseExactMatch();
            }

            if (token == null) {
                token = parseRegular();
            }
        }

        return token;
    }

    private DOPToken parseRegular() {
        int position = currentPosition;
        char c = source.charAt(position);

        while (!Character.isWhitespace(c)) {
            position++;
            if (position >= maxPosition) {
                break;
            }

            c = source.charAt(position);
        }

        RegularToken regularToken = new RegularToken(source.substring(currentPosition, position));

        // Update global position
        currentPosition = position;

        return regularToken;
    }

    private DOPToken parseExactMatch() {
        int position = currentPosition;
        int tokenStartPos = position + 1;
        DOPToken token = null;
        char c = source.charAt(position);

        if (c == '"' && source.indexOf('"', ++position) != -1) {
            while (source.charAt(position) != '"') {
                position++;
            }

            token = new ExactMatchToken(source.substring(tokenStartPos, position));

            // Consumes the closing "
            position++;

            // Update global position
            currentPosition = position;
        }

        return token;
    }

    private int skipWhiteSpaces(int startPosition) {
        int position = startPosition;

        while (position < maxPosition && Character.isWhitespace(source.charAt(position))) {
            position++;
        }

        return position;
    }
}
