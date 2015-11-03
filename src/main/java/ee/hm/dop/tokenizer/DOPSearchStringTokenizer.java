package ee.hm.dop.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class DOPSearchStringTokenizer {

    private static final String AUTHOR_KEYWORD = "author:";
    private static final String TITLE_KEYWORD = "title:";
    private static final String DESCRIPTION_KEYWORD = "description:";
    private static final String SUMMARY_KEYWORD = "summary:";
    private static final char QUOTES = '"';
    private static final char OPENING_PARENTHESES = '(';
    private static final char CLOSING_PARENTHESES = ')';

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
            } else if (c == OPENING_PARENTHESES) {
                token = parseParentheses();
            } else if (c == 'a') {
                token = parseAuthor();
                if (token == null) {
                    token = parseAnd();
                }
            } else if (c == 'd') {
                token = parseDescription();
            } else if (c == 's') {
                token = parseSummary();
            } else if (c == 't') {
                token = parseTitle();
            } else if (c == '+') {
                token = parseMustHave();
            } else if (c == '-') {
                token = parseMustNotHave();
            } else if (c == 'o') {
                token = parseOr();
            }

            if (token == null) {
                token = parseRegular();
            }
        }

        return token;
    }

    private DOPToken parseMustHave() {
        return new MustHaveToken(extractInnerTokenValue());
    }

    private DOPToken parseMustNotHave() {
        return new MustNotHaveToken(extractInnerTokenValue());
    }

    private DOPToken extractInnerTokenValue() {
        DOPToken content = null;

        currentPosition++;
        if (currentPosition < maxPosition) {
            char c = source.charAt(currentPosition);

            if (!Character.isWhitespace(c)) {
                content = parse();
            }
        }

        return content;
    }

    private DOPToken parseAuthor() {
        String value = extractTokenValue(AUTHOR_KEYWORD);
        if (value == null) {
            return null;
        }

        return new AuthorToken(value);
    }

    private DOPToken parseDescription() {
        String value = extractTokenValue(DESCRIPTION_KEYWORD);
        if (value == null) {
            return null;
        }

        return new DescriptionToken(value);
    }

    private DOPToken parseSummary() {
        String value = extractTokenValue(SUMMARY_KEYWORD);
        if (value == null) {
            return null;
        }

        return new SummaryToken(value);
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
                position = nextWhitespace(position);
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

    private int getClosingParentheses(int position) {
        Stack<Integer> openingParentheses = new Stack<>();
        openingParentheses.push(position - 1);

        boolean quotesOpen = false;
        while (position < maxPosition) {
            switch (source.charAt(position)) {
                case OPENING_PARENTHESES:
                    if (!quotesOpen) {
                        openingParentheses.push(position);
                    }
                    break;
                case CLOSING_PARENTHESES:
                    if (!quotesOpen) {
                        openingParentheses.pop();
                        if (openingParentheses.isEmpty()) {
                            return position;
                        }
                    }
                    break;
                case QUOTES:
                    quotesOpen = !quotesOpen;
                    break;
                default:
                    break;
            }
            position++;
        }
        return -1;
    }

    private int nextWhitespace(int startPosition) {
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
        int position = nextWhitespace(currentPosition);
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

    private DOPToken parseParentheses() {
        DOPToken token = null;
        int position = currentPosition;
        char c = source.charAt(position++);

        int closingParentheses = getClosingParentheses(position);
        if (c == OPENING_PARENTHESES && closingParentheses != -1) {
            String innerSource = source.substring(position, closingParentheses);
            token = new ParenthesesToken(getInnerTokens(innerSource));

            // Consumes the closing parentheses
            position = closingParentheses + 1;

            // Update global position
            currentPosition = position;
        }

        return token;
    }

    private List<DOPToken> getInnerTokens(String innerSource) {
        DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(innerSource);
        List<DOPToken> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    private DOPToken parseAnd() {
        int position = nextWhitespace(currentPosition);
        String value = source.substring(currentPosition, position).toUpperCase();

        if (!value.equals("AND")) {
            return null;
        }

        // Update global position
        currentPosition = position;

        return new AndToken();
    }

    private DOPToken parseOr() {
        int position = nextWhitespace(currentPosition);
        String value = source.substring(currentPosition, position).toUpperCase();

        if (!value.equals("OR")) {
            return null;
        }

        // Update global position
        currentPosition = position;

        return new OrToken();
    }

    private int skipWhiteSpaces(int startPosition) {
        int position = startPosition;

        while (position < maxPosition && Character.isWhitespace(source.charAt(position))) {
            position++;
        }

        return position;
    }
}
