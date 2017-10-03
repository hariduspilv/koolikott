package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Author;
import org.junit.Test;

/**
 * Created by mart on 28.10.15.
 */
public class AuthorDaoTest extends DatabaseTestBase {

    @Inject
    private AuthorDao authorDao;

    @Test
    public void findAuthorByFullName() {
        Long id = 3L;
        String name = "Leonardo";
        String surname = "Fibonacci";

        Author author = authorDao.findAuthorByFullName(name, surname);
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

        Author created = authorDao.createOrUpdate(author);
        Author newAuthor = authorDao.findAuthorByFullName(created.getName(), created.getSurname());
        assertEquals(created.getId(), newAuthor.getId());
        assertEquals(created.getName(), newAuthor.getName());
        assertEquals(created.getSurname(), newAuthor.getSurname());
    }
}
