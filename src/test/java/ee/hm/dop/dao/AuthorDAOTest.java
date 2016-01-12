package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Author;

/**
 * Created by mart on 28.10.15.
 */
public class AuthorDAOTest extends DatabaseTestBase {

    @Inject
    private AuthorDAO authorDAO;

    @Test
    public void findAuthorByFullName() {
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

    @Test
    public void create() {
        Author author = new Author();
        author.setName("Illimar");
        author.setSurname("Onomatöpöa");

        Author created = authorDAO.create(author);

        Author newAuthor = authorDAO.findAuthorByFullName(created.getName(), created.getSurname());

        assertEquals(created.getId(), newAuthor.getId());
        assertEquals(created.getName(), newAuthor.getName());
        assertEquals(created.getSurname(), newAuthor.getSurname());
    }
}
