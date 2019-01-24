package ee.hm.dop.service.author;

import javax.inject.Inject;

import ee.hm.dop.dao.AuthorDao;
import ee.hm.dop.model.Author;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthorService {

    @Inject
    private AuthorDao authorDao;

    public Author getAuthorByFullName(String name, String surname) {
        return authorDao.findAuthorByFullName(name, surname);
    }

    public Author createAuthor(String name, String surname) {
        Author author = new Author();
        author.setName(name);
        author.setSurname(surname);
        return authorDao.createOrUpdate(author);
    }
}
