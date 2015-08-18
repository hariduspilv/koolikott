package ee.hm.dop.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class LanguageTest {

    @Test
    public void equalsSameObject() {
        Language language = new Language();
        assertTrue(language.equals(language));
    }

    @Test
    public void equalsPassingNUll() {
        Language language = new Language();
        assertFalse(language.equals(null));
    }

    @Test
    public void equalsNotLanguage() {
        Language language = new Language();
        assertFalse(language.equals(new Object()));
    }

    @Test
    public void equalsAndHashContract() {
        Language language = new Language();
        Language other = new Language();

        assertTrue(language.equals(other));
        assertEquals(language.hashCode(), other.hashCode());

        language.setId(10l);
        language.setCode("AAA");
        language.setCodes(new ArrayList<String>());

        assertFalse(language.equals(other));
        assertNotEquals(language.hashCode(), other.hashCode());

        language.setName("language");

        assertFalse(language.equals(other));
        assertNotEquals(language.hashCode(), other.hashCode());

        other.setName("other");

        assertFalse(language.equals(other));
        assertNotEquals(language.hashCode(), other.hashCode());

        other.setName("language");

        assertFalse(language.equals(other));
        assertNotEquals(language.hashCode(), other.hashCode());
    }

}
