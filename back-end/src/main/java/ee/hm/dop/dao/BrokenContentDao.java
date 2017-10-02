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

    public List<BrokenContent> getBrokenMaterials(User user) {
        return getEntityManager().createNativeQuery("SELECT b.* FROM (\n" +
                "       SELECT b.*\n" +
                "       FROM BrokenContent b\n" +
                "         JOIN LearningObject o ON b.material = o.id\n" +
                "         JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                "         JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                "       WHERE o.deleted = 0\n" +
                "         AND b.deleted = 0" +
                "         AND ut.user = :userId\n" +
                "     ) b ORDER BY added ASC, id ASC", entity())
                .setParameter("userId", user.getId())
                .getResultList();
    }

    public long getBrokenCount() {
        String queryString = "SELECT Count(b.id) FROM BrokenContent b " +
                "INNER JOIN LearningObject lo ON b.material=lo.id " +
                "WHERE lo.deleted = FALSE AND b.deleted = FALSE";
        Query query = getEntityManager().createNativeQuery(queryString);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    public long getBrokenCount(User user) {
        Query query = getEntityManager()
                .createNativeQuery("SELECT Count(b.id) FROM BrokenContent b " +
                        "INNER JOIN LearningObject lo ON b.material=lo.id " +
                        "INNER JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n" +
                        "INNER JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "WHERE lo.deleted = 0 " +
                        "AND b.deleted = 0 " +
                        "AND ut.user = :userId\n")
                .setParameter("userId", user.getId());
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
