package ee.hm.dop.tokenizer;

import java.util.NoSuchElementException;

public class DOPSearchStringTokenizer {

    private static final String AUTHOR_KEYWORD = "author:";
    private static final String TITLE_KEYWORD = "title:";
    private static final char QUOTES = '"';

    private String source;
    private int currentPosition;
    private int newPosition;
    private int maxPosition;

    public DOPSearchStringTokenizer(String source) {
        this.source = source.trim().toLowerCase();
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

            if (c == QUOTES) {
                token = parseExactMatch();
            } else if (c == 'a') {
                token = parseAuthor();
            } else if (c == 't') {
                token = parseTitle();
            } else if (c == '+') {
                token = parseMustHave();
            }

            if (token == null) {
                token = parseRegular();
            }
        }

        return token;
    }

    private DOPToken parseMustHave() {
        DOPToken content = null;

        currentPosition++;
        if (currentPosition < maxPosition) {
            char c = source.charAt(currentPosition);

            if (!Character.isWhitespace(c)) {
                content = parse();
            }
        }

        return new MustHaveToken(content);
    }

    private DOPToken parseAuthor() {
        String value = extractTokenValue(AUTHOR_KEYWORD);
        if (value == null) {
            return null;
        }

        return new AuthorToken(value);
    }

    private DOPToken parseTitle() {
        String value = extractTokenValue(TITLE_KEYWORD);
        if (value == null) {
            return null;
        }

        return new TitleToken(value);
    }

    private String extractTokenValue(String keyword) {
        int position = currentPosition;

        if (position + keyword.length() - 1 >= maxPosition) {
            return null;
        }

        String value = null;
        char c = source.charAt(position);

        if (startsWithKeyword(keyword, position)) {
            position += keyword.length();
            c = source.charAt(position);
            if (c == QUOTES) {
                position++;
            }

            int tokenStartPos = position;
            int closingQuotes = getClosingQuotes(position);
            if (c == QUOTES && closingQuotes != -1) {
                value = source.substring(tokenStartPos, closingQuotes);

                // Consumes the closing "
                position = closingQuotes + 1;
            } else {
                position = nextWhiteSpace(position);
                value = source.substring(tokenStartPos, position);
            }

            // Update global position
            currentPosition = position;
        }

        return value;
    }

    private int getClosingQuotes(int position) {
        return source.indexOf(QUOTES, position);
    }

    private int nextWhiteSpace(int startPosition) {
        int position = startPosition;
        char c = source.charAt(position);

        while (!Character.isWhitespace(c)) {
            position++;
            if (position >= maxPosition) {
                break;
            }

            c = source.charAt(position);
        }

        return position;
    }

    private boolean startsWithKeyword(String keyword, int position) {
        return source.substring(position, position + keyword.length()).equals(keyword);
    }

    private DOPToken parseRegular() {
        int position = nextWhiteSpace(currentPosition);
        RegularToken regularToken = new RegularToken(source.substring(currentPosition, position));

        // Update global position
        currentPosition = position;

        return regularToken;
    }

    private DOPToken parseExactMatch() {
        DOPToken token = null;
        int position = currentPosition;
        char c = source.charAt(position++);

        int closingQuotes = getClosingQuotes(position);
        if (c == QUOTES && closingQuotes != -1) {
            token = new ExactMatchToken(source.substring(position, closingQuotes));

            // Consumes the closing "
            position = closingQuotes + 1;

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
