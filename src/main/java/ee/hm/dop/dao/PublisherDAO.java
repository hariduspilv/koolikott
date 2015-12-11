package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Publisher;

/**
 * Created by mart on 2.12.15.
 */
public class PublisherDAO {

    @Inject
    private EntityManager entityManager;

    public Publisher findPublisherByName(String name) {
        TypedQuery<Publisher> findByName = entityManager.createQuery("SELECT p FROM Publisher p WHERE p.name = :name ",
                Publisher.class);

        Publisher publisher = null;
        try {
            publisher = findByName.setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return publisher;
    }

    public Publisher create(Publisher publisher) {
        Publisher merged = entityManager.merge(publisher);
        entityManager.persist(merged);
        return merged;
    }
}
