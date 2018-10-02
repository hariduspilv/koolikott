package ee.hm.dop.utils.tokenizer;

import ezvcard.property.Title;
import org.junit.Test;

import static ee.hm.dop.utils.tokenizer.TitleUtils.MAX_TITLE_LENGTH;
import static org.junit.Assert.*;

public class TitleUtilsTest {

    @Test
    public void empty_title_returns_empty_titleUrl() {
        assertEquals("", TitleUtils.makeEncodingFriendly(null));
        assertEquals("", TitleUtils.makeEncodingFriendly(""));
        assertEquals("", TitleUtils.makeEncodingFriendly(" "));
        assertEquals("", TitleUtils.makeEncodingFriendly("           "));
    }

    @Test
    public void one_word_title_remains_the_same() {
        assertEquals("BestTitle", TitleUtils.makeEncodingFriendly("BestTitle"));
    }

    @Test
    public void two_word_title_has_break_replaced_with_underscrore() {
        assertEquals("Best_Title", TitleUtils.makeEncodingFriendly("Best Title"));
    }

    @Test
    public void trailing_space_is_trimmed() {
        assertEquals("BestTitle", TitleUtils.makeEncodingFriendly("BestTitle            "));
    }

    @Test
    public void leading_space_is_trimmed() {
        assertEquals("BestTitle", TitleUtils.makeEncodingFriendly("         BestTitle            "));
    }

    @Test
    public void length_is_predefined() {
        assertEquals(MAX_TITLE_LENGTH, TitleUtils.makeEncodingFriendly("material title string length is predefined").length());
    }

    @Test
    public void title_diacritics_are_replaced_with_standard_chars() {
        assertEquals("AOUOaouo", TitleUtils.makeEncodingFriendly("ÄÖÜÕäöüõ"));
    }

    @Test
    public void title_with_russian_characters_russian_characters_will_not_be_replaced() {
        assertEquals("это_русскии_заголово", TitleUtils.makeEncodingFriendly("это русский заголовок"));
    }

    @Test
    public void title_with_japanese_characters() {
        assertEquals("ランダムテキスト", TitleUtils.makeEncodingFriendly("ランダムテキスト"));
    }

    @Test
    public void title_with_arabic_characters() {
        assertEquals("العنوان_العربي", TitleUtils.makeEncodingFriendly("العنوان العربي"));
    }

    @Test
    public void title_symbols_replaced_with_underscores_more_than_one_consecutive_underscore_replaced_with_single() {
        assertEquals("Title_", TitleUtils.makeEncodingFriendly("Title @£¤$%&/{[]}()=)"));
    }

    @Test
    public void title_with_long_space_between_words_multiple_underscore_replaced_with_single() {
        assertEquals("Best_Title", TitleUtils.makeEncodingFriendly("Best       Title"));
    }
}