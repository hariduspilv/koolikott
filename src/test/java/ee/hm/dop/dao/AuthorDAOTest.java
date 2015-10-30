package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Author;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mart on 28.10.15.
 */
public class AuthorDAOTest extends DatabaseTestBase {

    @Inject
    private AuthorDAO authorDAO;

    @Test
    public void findResourceTypeByName() {
        Long id = (long) 3;
        String name = "Leonardo";
        String surname = "Fibonacci";

        Author author = authorDAO.findAuthorByFullName(name, surname);
        assertNotNull(author);
        assertNotNull(author.getId());
        assertEquals(id, author.getId());
        assertEquals(name, author.getName());
        assertEquals(surname, author.getSurname());
    }
}
