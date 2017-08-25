package ee.hm.dop.dao;

import ee.hm.dop.model.Author;

public class AuthorDao extends AbstractDao<Author> {

    public Author findAuthorByFullName(String name, String surname) {
        return findByField("name", name, "surname", surname);
    }
}
