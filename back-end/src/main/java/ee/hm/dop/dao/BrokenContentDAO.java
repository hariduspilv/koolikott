package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.User;
import org.joda.time.DateTime;

public class BrokenContentDAO {

    @Inject
    private EntityManager entityManager;

    public BrokenContent update(BrokenContent brokenontent) {
        if (brokenontent.getId() == null) {
            brokenontent.setAdded(DateTime.now());
        }
        BrokenContent merged;
        merged = entityManager.merge(brokenontent);
        entityManager.persist(merged);
        return merged;
    }

    public void deleteBrokenMaterials(Long id) {
        Query query = entityManager
                .createQuery("UPDATE BrokenContent b set b.deleted = true WHERE b.material.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    public List<BrokenContent> getBrokenMaterials() {
        return entityManager.createQuery("FROM BrokenContent b WHERE b.material IS NOT NULL AND b.deleted = false",
                BrokenContent.class).getResultList();
    }

    public List<BrokenContent> findByMaterialAndUser(long materialId, User loggedInUser) {
        TypedQuery<BrokenContent> findByData = entityManager.createQuery(
                "FROM BrokenContent b WHERE b.material.id = :materialid AND b.creator = :user AND b.deleted = false",
                BrokenContent.class);

        List<BrokenContent> brokenContents = null;
        try {
            brokenContents = findByData.setParameter("materialid", materialId).setParameter("user", loggedInUser)
                    .getResultList();
        } catch (NoResultException ex) {
            // ignore
        }

        return brokenContents;
    }

    public List<BrokenContent> findByMaterial(long materialId) {
        TypedQuery<BrokenContent> findByMaterial = entityManager.createQuery(
                "FROM BrokenContent b WHERE b.material.id = :materialid AND b.deleted = false", BrokenContent.class);

        List<BrokenContent> brokenContents = null;
        try {
            brokenContents = findByMaterial.setParameter("materialid", materialId).getResultList();
        } catch (NoResultException ex) {
            // ignore
        }

        return brokenContents;
    }

}
