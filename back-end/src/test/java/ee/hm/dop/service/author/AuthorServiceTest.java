package ee.hm.dop.service.author;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import ee.hm.dop.dao.AuthorDao;
import ee.hm.dop.model.Author;
import ee.hm.dop.service.author.AuthorService;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class AuthorServiceTest {

    @TestSubject
    private AuthorService authorService = new AuthorService();

    @Mock
    private AuthorDao authorDao;

    @Test
    public void getAuthorByFullName() {
        Author author = getAuthor();

        expect(authorDao.findAuthorByFullName(author.getName(), author.getSurname())).andReturn(author);

        replay(authorDao);

        Author returned = authorService.getAuthorByFullName(author.getName(), author.getSurname());

        verify(authorDao);

        assertEquals(author.getName(), returned.getName());
        assertEquals(author.getSurname(), returned.getSurname());
    }

    @Test
    public void createAuthor() {
        Author author = getAuthor();

        expect(authorDao.createOrUpdate(anyObject(Author.class))).andReturn(author);

        replay(authorDao);

        Author returned = authorService.createAuthor(author.getName(), author.getSurname());

        verify(authorDao);

        assertEquals(author.getName(), returned.getName());
        assertEquals(author.getSurname(), returned.getSurname());
    }

    private Author getAuthor() {
        Author author = new Author();
        author.setName("firstName");
        author.setSurname("lastName");
        return author;
    }
}
