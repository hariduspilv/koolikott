package ee.hm.dop.dao;

import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.User;
import org.joda.time.DateTime;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

public class BrokenContentDao extends AbstractDao<BrokenContent> {

    public BrokenContent update(BrokenContent brokenContent) {
        if (brokenContent.getId() == null) {
            brokenContent.setAdded(DateTime.now());
        }
        return createOrUpdate(brokenContent);
    }

    public void deleteBrokenMaterials(Long materialId) {
        Query query = getEntityManager().createQuery("UPDATE BrokenContent b set b.deleted = true WHERE b.material.id = :id");
        query.setParameter("id", materialId);
        query.executeUpdate();
    }

    public List<BrokenContent> getBrokenMaterials() {
        return getEntityManager().createQuery("" +
                "SELECT b FROM BrokenContent b " +
                "INNER JOIN b.material m " +
                "WHERE m.deleted = false " +
                "AND b.deleted = false", entity())
                .getResultList();
    }

    public long getBrokenCount() {
        String queryString = "SELECT Count(b.id) FROM BrokenContent b " +
                "INNER JOIN LearningObject lo ON b.material=lo.id " +
                "WHERE lo.deleted = FALSE AND b.deleted = FALSE";
        Query query = getEntityManager().createNativeQuery(queryString);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    public List<BrokenContent> findByMaterialAndUser(long materialId, User loggedInUser) {
        return getEntityManager()
                .createQuery("FROM BrokenContent b " +
                        "WHERE b.material.id = :materialid " +
                        "AND b.creator = :user " +
                        "AND b.deleted = false", entity())
                .setParameter("materialid", materialId)
                .setParameter("user", loggedInUser)
                .getResultList();

    }

    public List<BrokenContent> findByMaterial(long materialId) {
        return getEntityManager()
                .createQuery("FROM BrokenContent b " +
                        "WHERE b.material.id = :materialid " +
                        "AND b.deleted = false", entity())
                .setParameter("materialid", materialId)
                .getResultList();

    }

}
