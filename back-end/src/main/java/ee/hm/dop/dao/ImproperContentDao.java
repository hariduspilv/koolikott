package ee.hm.dop.dao;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImproperContentDao extends AbstractDao<ImproperContent> {

    @Override
    public ImproperContent findById(Long improperContentId) {
        TypedQuery<ImproperContent> findById = getEntityManager().createQuery(
                "FROM ImproperContent i WHERE i.id = :id AND i.deleted = false", entity())
                .setParameter("id", improperContentId);
        return getSingleResult(findById);
    }

    @Override
    public List<ImproperContent> findAll() {
        return getEntityManager().createQuery("FROM ImproperContent i WHERE i.deleted = false", entity())
                .getResultList();
    }

    public ImproperContent findByLearningObjectAndCreator(LearningObject learningObject, User creator) {
        TypedQuery<ImproperContent> findByLearningObjectAndUser = getEntityManager().createQuery(
                "FROM ImproperContent i WHERE i.learningObject = :learningObject AND i.creator = :user AND i.deleted = false", entity())
                .setParameter("learningObject", learningObject)
                .setParameter("user", creator);
        return getSingleResult(findByLearningObjectAndUser);
    }

    public List<ImproperContent> findByLearningObject(LearningObject learningObject) {
        return getEntityManager().createQuery("FROM ImproperContent i WHERE i.learningObject = :learningObject AND i.deleted = false", entity())
                .setParameter("learningObject", learningObject)
                .getResultList();
    }

    public void deleteAll(List<ImproperContent> impropers) {
        List<Long> ids = impropers.stream().map(ImproperContent::getId).collect(Collectors.toList());
        getEntityManager().createQuery("update ImproperContent i set i.deleted = true where i.id in :impropers")
                .setParameter("impropers", ids) //
                .executeUpdate();
    }

    public long getImproperPortfolioCount() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT Count(DISTINCT imp.learningObject) FROM ImproperContent imp " +
                        "INNER JOIN Portfolio p ON imp.learningObject=p.id " +
                        "INNER JOIN LearningObject lo ON lo.id=p.id " +
                        "WHERE imp.deleted = false AND lo.deleted=false")
                .getSingleResult())
                .longValue();
    }

    public long getImproperMaterialCount() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT Count(DISTINCT imp.learningObject) FROM ImproperContent imp " +
                        "INNER JOIN Material m ON imp.learningObject=m.id " +
                        "INNER JOIN LearningObject lo ON lo.id=m.id " +
                        "WHERE imp.deleted = false AND lo.deleted=false")
                .getSingleResult())
                .longValue();
    }
}