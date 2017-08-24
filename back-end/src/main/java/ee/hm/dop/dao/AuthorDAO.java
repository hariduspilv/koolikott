package ee.hm.dop.dao;

import ee.hm.dop.model.Author;

public class AuthorDAO extends NewBaseDao<Author>{

    @Override
    public Class<Author> entity() {
        return Author.class;
    }

    public Author findAuthorByFullName(String name, String surname) {
        return getByField("name", name, "surname", surname);
    }
}
