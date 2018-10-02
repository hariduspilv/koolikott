package ee.hm.dop.utils.tokenizer;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;

import static java.text.Normalizer.normalize;
import static org.apache.commons.lang3.StringUtils.*;

public class TitleUtils {

    public static final int MAX_TITLE_LENGTH = 20;

    public static String makeEncodingFriendly(String title) {
        return isNotBlank(title) ? left(replaceChars(title), MAX_TITLE_LENGTH) : "";
    }

    public static String replaceChars(String title) {
        return normalize(trim(title), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[\\s+\\p{P}\\p{S}]", "_");
    }
}
