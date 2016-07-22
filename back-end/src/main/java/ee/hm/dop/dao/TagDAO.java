package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Tag;

public class TagDAO {

    @Inject
    private EntityManager entityManager;

    public Tag findTagByName(String name) {
        TypedQuery<Tag> findByName = entityManager.createQuery("SELECT t FROM Tag t WHERE t.name = :name", Tag.class);

        Tag tag = null;
        try {
            tag = findByName.setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return tag;
    }
}
