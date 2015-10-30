package ee.hm.dop.dao;

import ee.hm.dop.model.Author;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Created by mart on 28.10.15.
 */
public class AuthorDAO {

    @Inject
    private EntityManager entityManager;

    public Author findAuthorByFullName(String name, String surname) {
        TypedQuery<Author> findByName = entityManager.createQuery("SELECT a FROM Author a WHERE a.name = :name and a.surname = :surname", Author.class);

        Author author = null;
        try {
            author = findByName.setParameter("name", name).setParameter("surname", surname).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return author;
    }

    public Author create(Author author) {
        Author merged = entityManager.merge(author);
        entityManager.persist(merged);
        return merged;
    }
}
