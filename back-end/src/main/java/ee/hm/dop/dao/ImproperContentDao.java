package ee.hm.dop.dao;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

import java.math.BigInteger;
import java.util.List;

public class ImproperContentDao extends AbstractDao<ImproperContent> {

    public ImproperContent findByLearningObjectAndCreator(LearningObject learningObject, User creator) {
        return findByField("learningObject", learningObject, "creator", creator, "reviewed", false);
    }

    public ImproperContent findByIdUnreviewed(Long id) {
        return findByField("id", id, "reviewed", false);
    }

    public List<ImproperContent> findAllUnreviewed() {
        return findByFieldList("reviewed", false);
    }

    public List<ImproperContent> findAllImproperContentPortfolio() {
        return (List<ImproperContent>) getEntityManager().createNativeQuery("SELECT imp.* FROM ImproperContent imp " +
                "INNER JOIN Portfolio p ON imp.learningObject=p.id " +
                "INNER JOIN LearningObject lo ON lo.id=p.id " +
                "WHERE imp.reviewed = FALSE " +
                "AND lo.deleted=FALSE", entity())
                .getResultList();
    }

    public List<ImproperContent> findAllImproperContentPortfolio(User user) {
        return (List<ImproperContent>) getEntityManager().createNativeQuery(
                "SELECT imp.* FROM ImproperContent imp " +
                        "INNER JOIN LearningObject lo ON imp.learningObject=lo.id " +
                        "INNER JOIN Portfolio p ON lo.id=p.id " +
                        "INNER JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id " +
                        "INNER JOIN User_Taxon ut ON ut.taxon = lt.taxon " +
                        "WHERE ut.user = :userId " +
                        "AND imp.reviewed = 0 " +
                        "AND lo.deleted=0", entity())
                .setParameter("userId", user.getId())
                .getResultList();
    }

    public List<ImproperContent> findAllImproperContentMaterial() {
        return (List<ImproperContent>) getEntityManager().createNativeQuery("SELECT imp.* FROM ImproperContent imp " +
                "INNER JOIN Material m ON imp.learningObject=m.id " +
                "INNER JOIN LearningObject lo ON lo.id=m.id " +
                "WHERE imp.reviewed = FALSE " +
                "AND lo.deleted=FALSE", entity())
                .getResultList();
    }

    public List<ImproperContent> findAllImproperContentMaterial(User user) {
        return (List<ImproperContent>) getEntityManager().createNativeQuery(
                "SELECT imp.* FROM ImproperContent imp " +
                        "INNER JOIN LearningObject lo ON imp.learningObject=lo.id " +
                        "INNER JOIN Material m ON lo.id=m.id " +
                        "INNER JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id " +
                        "INNER JOIN User_Taxon ut ON ut.taxon = lt.taxon " +
                        "WHERE ut.user = :userId " +
                        "AND imp.reviewed = 0 " +
                        "AND lo.deleted=0", entity())
                .setParameter("userId", user.getId())
                .getResultList();
    }

    public List<ImproperContent> findByLearningObject(LearningObject learningObject) {
        return findByFieldList("learningObject", learningObject, "reviewed", false);
    }

    public long getImproperPortfolioCount() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT Count(DISTINCT imp.learningObject) FROM ImproperContent imp " +
                        "INNER JOIN Portfolio p ON imp.learningObject=p.id " +
                        "INNER JOIN LearningObject lo ON lo.id=p.id " +
                        "WHERE imp.reviewed = FALSE " +
                        "AND lo.deleted=FALSE")
                .getSingleResult())
                .longValue();
    }

    public long getImproperPortfolioCount(User user) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT Count(DISTINCT lo.id) FROM ImproperContent ic \n" +
                        "INNER JOIN LearningObject lo ON ic.learningObject=lo.id \n" +
                        "INNER JOIN Portfolio p ON lo.id=p.id " +
                        "INNER JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id \n" +
                        "INNER JOIN User_Taxon ut ON ut.taxon = lt.taxon \n" +
                        "WHERE " +
                        "ut.user = :userId \n" +
                        "AND " +
                        "ic.reviewed = 0 \n" +
                        "AND lo.deleted=0")
                .setParameter("userId", user.getId())
                .getSingleResult())
                .longValue();
    }

    public long getImproperMaterialCount() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT Count(DISTINCT imp.learningObject) FROM ImproperContent imp " +
                        "INNER JOIN Material m ON imp.learningObject=m.id " +
                        "INNER JOIN LearningObject lo ON lo.id=m.id " +
                        "WHERE imp.reviewed = FALSE " +
                        "AND lo.deleted=FALSE")
                .getSingleResult())
                .longValue();
    }

    public long getImproperMaterialCount(User user) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT Count(DISTINCT lo.id) FROM ImproperContent ic \n" +
                        "INNER JOIN LearningObject lo ON ic.learningObject=lo.id \n" +
                        "INNER JOIN Material m ON lo.id=m.id " +
                        "INNER JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id \n" +
                        "INNER JOIN User_Taxon ut ON ut.taxon = lt.taxon \n" +
                        "WHERE " +
                        "ut.user = :userId \n" +
                        "AND " +
                        "ic.reviewed = 0 \n" +
                        "AND lo.deleted=0")
                .setParameter("userId", user.getId())
                .getSingleResult())
                .longValue();
    }
}