package ee.hm.dop.tokenizer;

import java.util.ArrayList;

public class DOPSearchStringTokenizer {

    private ArrayList<DOPToken> tokens;

    public DOPSearchStringTokenizer(String str) {
        tokens = new ArrayList<DOPToken>();
        parse(str);
    }

    public int countTokens() {
        return tokens.size();
    }

    public boolean hasMoreTokens() {
        return !tokens.isEmpty();
    }

    public DOPToken nextToken() {
        return tokens.remove(0);
    }

    private void parse(String s) {

        StringBuilder sb = new StringBuilder();
        boolean readingToken = false;
        boolean readingExactMatch = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {

            case ' ':
                if (readingToken) {
                    if (readingExactMatch) {
                        sb.append(c);
                    } else {
                        createRegularToken(sb);
                        readingToken = false;
                    }
                }
                break;

            case '"':
                if (readingExactMatch) {
                    createExactMatchToken(sb);
                    readingExactMatch = false;
                    readingToken = false;
                } else if (s.indexOf('"', i + 1) == -1) {
                    // Case when there is no closing "
                    readingToken = true;
                    sb.append(c);
                } else {
                    if (readingToken) {
                        createRegularToken(sb);
                    }
                    readingToken = true;
                    readingExactMatch = true;
                }
                break;

            default:
                readingToken = true;
                sb.append(c);
            }
        }

        createRegularToken(sb);
    }

    private void createRegularToken(StringBuilder sb) {
        if (sb.length() > 0) {
            tokens.add(new RegularToken(sb.toString()));
            sb.setLength(0);
        }
    }

    private void createExactMatchToken(StringBuilder sb) {
        if (sb.length() > 0) {
            tokens.add(new ExactMatchToken(sb.toString()));
            sb.setLength(0);
        }
    }
}
