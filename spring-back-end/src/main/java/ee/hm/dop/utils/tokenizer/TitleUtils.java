package ee.hm.dop.utils.tokenizer;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;

import static java.text.Normalizer.normalize;
import static org.apache.commons.lang3.StringUtils.*;

public class TitleUtils {

    public static final int MAX_TITLE_LENGTH = 30;
    public static final String SPACES_PUNCTUATION_SYMBOLS = "[\\s+\\p{P}\\p{S}]";
    public static final String CONSECUTIVE_UNDERSCORES = "\\_+";
    public static final String DIACRITICAL_MARKS = "\\p{InCombiningDiacriticalMarks}+";

    public static String makeEncodingFriendly(String title) {
        return isNotBlank(title) ? left(replaceChars(title), MAX_TITLE_LENGTH) : "";
    }

    public static String replaceChars(String title) {
        return normalize(trim(title), Normalizer.Form.NFD)
                .replaceAll(DIACRITICAL_MARKS, "")
                .replaceAll(SPACES_PUNCTUATION_SYMBOLS, "-")
                .replaceAll(CONSECUTIVE_UNDERSCORES, "-");
    }
}
