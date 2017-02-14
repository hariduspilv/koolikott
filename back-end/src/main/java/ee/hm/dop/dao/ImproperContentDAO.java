package ee.hm.dop.dao;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ImproperContentDAO extends BaseDAO<ImproperContent> {

    public ImproperContent findById(long improperContentId) {
        TypedQuery<ImproperContent> findById = createQuery(
                "FROM ImproperContent i WHERE i.id = :id AND i.deleted = false", ImproperContent.class) //
                .setParameter("id", improperContentId);
        return getSingleResult(findById);
    }

    public List<ImproperContent> findAll() {
        return createQuery("FROM ImproperContent i WHERE i.deleted = false", ImproperContent.class).getResultList();
    }

    public ImproperContent findByLearningObjectAndCreator(LearningObject learningObject, User creator) {
        TypedQuery<ImproperContent> findByLearningObjectAndUser = createQuery(
                "FROM ImproperContent i WHERE i.learningObject = :learningObject AND i.creator = :user AND i.deleted = false",
                ImproperContent.class) //
                .setParameter("learningObject", learningObject) //
                .setParameter("user", creator);

        return getSingleResult(findByLearningObjectAndUser);
    }

    public List<ImproperContent> findByLearningObject(LearningObject learningObject) {
        return createQuery("FROM ImproperContent i WHERE i.learningObject = :learningObject AND i.deleted = false",
                ImproperContent.class) //
                .setParameter("learningObject", learningObject) //
                .getResultList();
    }

    public void deleteAll(List<ImproperContent> impropers) {
        List<Long> ids = new ArrayList<>();
        impropers.forEach(improper -> ids.add(improper.getId()));

        getEntityManager().createQuery("update ImproperContent i set i.deleted = true where i.id in :impropers") //
                .setParameter("impropers", ids) //
                .executeUpdate();
    }

    public long getImproperPortfolioCount() {
        String queryString = "SELECT Count(DISTINCT imp.learningObject) FROM ImproperContent imp INNER JOIN Portfolio p ON imp.learningObject=p.id INNER JOIN LearningObject lo ON lo.id=p.id WHERE imp.deleted = false AND lo.deleted=false";
        Query query = getEntityManager().createNativeQuery(queryString);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    public long getImproperMaterialCount() {
        String queryString = "SELECT Count(DISTINCT imp.learningObject) FROM ImproperContent imp INNER JOIN Material m ON imp.learningObject=m.id INNER JOIN LearningObject lo ON lo.id=m.id WHERE imp.deleted = false AND lo.deleted=false";
        Query query = getEntityManager().createNativeQuery(queryString);
        return ((BigInteger) query.getSingleResult()).longValue();
    }
}
