package ee.hm.dop.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RepositoryTest {

    @Test
    public void equals() {
        Repository repository = new Repository();

        assertTrue(repository.equals(repository));
        assertFalse(repository.equals(null));

        Repository repository2 = new Repository();
        assertTrue(repository.equals(repository2));

        repository.setId((long) 4);
        assertTrue(repository.equals(repository2));

        repository.setBaseURL("url.com");
        assertFalse(repository.equals(repository2));

        repository2.setBaseURL("url.com");
        assertTrue(repository.equals(repository2));

        assertFalse(repository.equals(new Material()));
    }

    @Test
    public void testHashCode() {
        Repository repository = new Repository();
        Repository repository2 = new Repository();
        assertEquals(repository.hashCode(), repository2.hashCode());

        repository.setId((long) 4);
        assertEquals(repository.hashCode(), repository2.hashCode());

        repository.setBaseURL("url.com");
        assertNotEquals(repository.hashCode(), repository2.hashCode());

        repository2.setBaseURL("url.com");
        assertEquals(repository.hashCode(), repository2.hashCode());
    }

}
