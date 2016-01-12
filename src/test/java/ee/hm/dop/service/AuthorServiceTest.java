package ee.hm.dop.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.AuthorDAO;
import ee.hm.dop.model.Author;

@RunWith(EasyMockRunner.class)
public class AuthorServiceTest {

    @TestSubject
    private AuthorService authorService = new AuthorService();

    @Mock
    private AuthorDAO authorDAO;

    @Test
    public void getAuthorByFullName() {
        Author author = getAuthor();

        expect(authorDAO.findAuthorByFullName(author.getName(), author.getSurname())).andReturn(author);

        replay(authorDAO);

        Author returned = authorService.getAuthorByFullName(author.getName(), author.getSurname());

        verify(authorDAO);

        assertEquals(author.getName(), returned.getName());
        assertEquals(author.getSurname(), returned.getSurname());
    }

    @Test
    public void createAuthor() {
        Author author = getAuthor();

        expect(authorDAO.create(anyObject(Author.class))).andReturn(author);

        replay(authorDAO);

        Author returned = authorService.createAuthor(author.getName(), author.getSurname());

        verify(authorDAO);

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
