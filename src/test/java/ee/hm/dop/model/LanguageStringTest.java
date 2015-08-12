package ee.hm.dop.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LanguageStringTest {

    @Test
    public void equalsSameObject() {
        LanguageString languageString = new LanguageString();
        assertTrue(languageString.equals(languageString));
    }

    @Test
    public void equalsPassingNUll() {
        LanguageString languageString = new LanguageString();
        assertFalse(languageString.equals(null));
    }

    @Test
    public void equalsNotLanguageString() {
        LanguageString languageString = new LanguageString();
        assertFalse(languageString.equals(new Object()));
    }

    @Test
    public void equalsAndHashContract() {
        LanguageString languageString = new LanguageString();
        LanguageString other = new LanguageString();

        assertTrue(languageString.equals(other));
        assertEquals(languageString.hashCode(), other.hashCode());

        languageString.setId(10l);

        assertTrue(languageString.equals(other));
        assertEquals(languageString.hashCode(), other.hashCode());

        Language language = new Language();
        language.setName("language");
        languageString.setLanguage(language);

        assertFalse(languageString.equals(other));
        assertNotEquals(languageString.hashCode(), other.hashCode());

        other.setLanguage(new Language());

        assertFalse(languageString.equals(other));
        assertNotEquals(languageString.hashCode(), other.hashCode());

        other.setLanguage(language);

        assertTrue(languageString.equals(other));
        assertEquals(languageString.hashCode(), other.hashCode());

        languageString.setText("Text 1");

        assertFalse(languageString.equals(other));
        assertNotEquals(languageString.hashCode(), other.hashCode());

        other.setText("Text 2");

        assertFalse(languageString.equals(other));
        assertNotEquals(languageString.hashCode(), other.hashCode());

        other.setText("Text 1");

        assertTrue(languageString.equals(other));
        assertEquals(languageString.hashCode(), other.hashCode());
    }
}
