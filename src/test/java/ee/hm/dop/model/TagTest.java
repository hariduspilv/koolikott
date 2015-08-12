package ee.hm.dop.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TagTest {

    @Test
    public void equalsSameObject() {
        Tag tag = new Tag();
        assertTrue(tag.equals(tag));
    }

    @Test
    public void equalsPassingNUll() {
        Tag tag = new Tag();
        assertFalse(tag.equals(null));
    }

    @Test
    public void equalsNotTag() {
        Tag tag = new Tag();
        assertFalse(tag.equals(new Object()));
    }

    @Test
    public void equalsAndHashContract() {
        Tag tag = new Tag();
        Tag other = new Tag();

        assertTrue(tag.equals(other));
        assertEquals(tag.hashCode(), other.hashCode());

        tag.setId(10l);

        assertTrue(tag.equals(other));
        assertEquals(tag.hashCode(), other.hashCode());

        tag.setName("tag");

        assertFalse(tag.equals(other));
        assertNotEquals(tag.hashCode(), other.hashCode());

        other.setName("other");

        assertFalse(tag.equals(other));
        assertNotEquals(tag.hashCode(), other.hashCode());

        other.setName("tag");

        assertTrue(tag.equals(other));
        assertEquals(tag.hashCode(), other.hashCode());
    }
}
