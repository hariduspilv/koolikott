package ee.hm.dop.dao;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

<<<<<<< HEAD
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
=======
import java.math.BigInteger;
import java.util.List;
>>>>>>> new-develop

public class ImproperContentDao extends AbstractDao<ImproperContent> {

    public ImproperContent findByLearningObjectAndCreator(LearningObject learningObject, User creator) {
<<<<<<< HEAD
        return findByField("learningObject", learningObject, "creator", creator, "deleted", false);
=======
        return findByField("learningObject", learningObject, "creator", creator, "reviewed", false);
    }

    public ImproperContent findByIdUnreviewed(Long id) {
        return findByField("id", id, "reviewed", false);
    }

    public List<ImproperContent> findAllUnreviewed() {
        return findByFieldList("reviewed", false);
>>>>>>> new-develop
    }

    public List<ImproperContent> findAllImproperContentPortfolio() {
        return (List<ImproperContent>) getEntityManager().createNativeQuery("SELECT imp.* FROM ImproperContent imp " +
                "INNER JOIN Portfolio p ON imp.learningObject=p.id " +
                "INNER JOIN LearningObject lo ON lo.id=p.id " +
<<<<<<< HEAD
                "WHERE imp.deleted = FALSE " +
=======
                "WHERE imp.reviewed = FALSE " +
>>>>>>> new-develop
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
<<<<<<< HEAD
                        "AND imp.deleted = 0 " +
=======
                        "AND imp.reviewed = 0 " +
>>>>>>> new-develop
                        "AND lo.deleted=0", entity())
                .setParameter("userId", user.getId())
                .getResultList();
    }

    public List<ImproperContent> findAllImproperContentMaterial() {
        return (List<ImproperContent>) getEntityManager().createNativeQuery("SELECT imp.* FROM ImproperContent imp " +
                "INNER JOIN Material m ON imp.learningObject=m.id " +
                "INNER JOIN LearningObject lo ON lo.id=m.id " +
<<<<<<< HEAD
                "WHERE imp.deleted = FALSE " +
=======
                "WHERE imp.reviewed = FALSE " +
>>>>>>> new-develop
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
<<<<<<< HEAD
                        "AND imp.deleted = 0 " +
=======
                        "AND imp.reviewed = 0 " +
>>>>>>> new-develop
                        "AND lo.deleted=0", entity())
                .setParameter("userId", user.getId())
                .getResultList();
    }

    public List<ImproperContent> findByLearningObject(LearningObject learningObject) {
<<<<<<< HEAD
        return findByFieldList("learningObject", learningObject, "deleted", false);
    }

    public void deleteAll(List<ImproperContent> impropers) {
        List<Long> ids = impropers.stream().map(ImproperContent::getId).collect(Collectors.toList());
        setDeleted(ids);
=======
        return findByFieldList("learningObject", learningObject, "reviewed", false);
>>>>>>> new-develop
    }

    public long getImproperPortfolioCount() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT Count(DISTINCT imp.learningObject) FROM ImproperContent imp " +
                        "INNER JOIN Portfolio p ON imp.learningObject=p.id " +
                        "INNER JOIN LearningObject lo ON lo.id=p.id " +
<<<<<<< HEAD
                        "WHERE imp.deleted = FALSE " +
=======
                        "WHERE imp.reviewed = FALSE " +
>>>>>>> new-develop
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
<<<<<<< HEAD
                        "ic.deleted = 0 \n" +
=======
                        "ic.reviewed = 0 \n" +
>>>>>>> new-develop
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
<<<<<<< HEAD
                        "WHERE imp.deleted = FALSE " +
=======
                        "WHERE imp.reviewed = FALSE " +
>>>>>>> new-develop
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
<<<<<<< HEAD
                        "ic.deleted = 0 \n" +
=======
                        "ic.reviewed = 0 \n" +
>>>>>>> new-develop
                        "AND lo.deleted=0")
                .setParameter("userId", user.getId())
                .getSingleResult())
                .longValue();
    }
}