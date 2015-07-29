package ee.hm.dop.utils;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import ee.hm.dop.utils.tokens.DOPToken;
import ee.hm.dop.utils.tokens.ExactMatchToken;
import ee.hm.dop.utils.tokens.RegularToken;

public class DOPSearchStringTokenizer {

    private static final String DOUBLE_QUOTE = "\"";
    private static final String WHITESPACE = " ";

    private ArrayList<DOPToken> tokens;

    public DOPSearchStringTokenizer(String str) {
        tokens = new ArrayList<DOPToken>();
        parseStringToTokens(str);
    }

    public String getWhitespaceSeparatedTokens() {
        return StringUtils.join(tokens, WHITESPACE);
    }

    private void parseStringToTokens(String str) {
        str = str.trim();
        if (str.length() > 0) {
            str = findExactMatchTokens(str);
            str = str.trim();
            str = str.replaceAll("\\s+", WHITESPACE);
            if (str.length() > 0) {
                findRegularTokens(str);
            }
        }
    }

    private void findRegularTokens(String str) {
        String[] regularTokens = str.split(WHITESPACE);
        for (int i = 0; i < regularTokens.length; i++) {
            tokens.add(new RegularToken(regularTokens[i]));
        }
    }

    private String findExactMatchTokens(String str) {
        int startIndex = str.indexOf(DOUBLE_QUOTE);
        while (startIndex > -1) {
            int endIndex = str.indexOf(DOUBLE_QUOTE, startIndex + 1);

            if (endIndex > -1) {
                str = extractExactMatchToken(str, startIndex, endIndex);
                startIndex = str.indexOf(DOUBLE_QUOTE);
            } else {
                break;
            }
        }

        return str;
    }

    private String extractExactMatchToken(String str, int startIndex, int endIndex) {
        String content = str.substring(startIndex + 1, endIndex);
        if (content.length() > 0) {
            tokens.add(new ExactMatchToken(content));
        }
        return str.substring(0, startIndex) + WHITESPACE + str.substring(endIndex + 1);
    }
}
