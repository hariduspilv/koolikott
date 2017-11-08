package ee.hm.dop.dao;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

import javax.persistence.TypedQuery;
import java.security.InvalidParameterException;
import java.util.List;

import static org.joda.time.DateTime.now;

public class LearningObjectDao extends AbstractDao<LearningObject> {

    private static final Object lock = new Object();

    @Override
    public Class<LearningObject> entity() {
        return LearningObject.class;
    }

    public LearningObject findByIdNotDeleted(long objectId) {
        TypedQuery<LearningObject> findByCode = getEntityManager()
                .createQuery("SELECT lo FROM LearningObject lo WHERE lo.id = :id AND lo.deleted = false", entity()) //
                .setParameter("id", objectId);
        return getSingleResult(findByCode);
    }

    public LearningObject findByIdDeleted(long objectId) {
        TypedQuery<LearningObject> findByCode = getEntityManager()
                .createQuery("SELECT lo FROM LearningObject lo WHERE lo.id = :id AND lo.deleted = true", entity()) //
                .setParameter("id", objectId);
        return getSingleResult(findByCode);
    }

    public LearningObject findById(long objectId) {
        TypedQuery<LearningObject> findByCode = getEntityManager()
                .createQuery("SELECT lo FROM LearningObject lo WHERE lo.id = :id", entity()) //
                .setParameter("id", objectId);
        return getSingleResult(findByCode);
    }

    public List<LearningObject> findAllById(List<Long> idList) {
        return getEntityManager()
                .createQuery("SELECT lo FROM LearningObject lo WHERE lo.deleted = false AND lo.id in :idList", entity())
                .setParameter("idList", idList)
                .getResultList();
    }

    public Long findAllNotDeleted() {
        return (Long) getEntityManager()
                .createQuery("SELECT count(lo) FROM LearningObject lo WHERE lo.deleted = false")
                .getSingleResult();
    }

    public List<LearningObject> findNewestLearningObjects(int numberOfLearningObjects, int startPosition) {
        return getEntityManager()
                .createQuery("FROM LearningObject lo WHERE lo.deleted = false ORDER BY added DESC, id DESC", entity())
                .setFirstResult(startPosition)
                .setMaxResults(numberOfLearningObjects)
                .getResultList();
    }

    public void delete(LearningObject learningObject) {
        setDeleted(learningObject, true);
    }

    public void restore(LearningObject learningObject) {
        setDeleted(learningObject, false);
    }

    private void setDeleted(LearningObject learningObject, boolean deleted) {
        if (learningObject.getId() == null) {
            throw new InvalidParameterException("LearningObject does not exist.");
        }
        learningObject.setDeleted(deleted);
        learningObject.setUpdated(now());
        createOrUpdate(learningObject);
    }

    public List<LearningObject> findByCreator(User creator, int start, int maxResults) {
        String queryString = "SELECT lo FROM LearningObject lo WHERE lo.creator.id = :creatorId AND lo.deleted = false order by added desc";
        return findByCreatorInner(creator, start, maxResults, queryString);
    }

    protected List<LearningObject> findByCreatorInner(User creator, int start, int maxResults, String queryString) {
        TypedQuery<LearningObject> typedQuery = getEntityManager()
                .createQuery(queryString, entity())
                .setParameter("creatorId", creator.getId())
                .setFirstResult(start);
        if (maxResults > 0) {
            typedQuery.setMaxResults(maxResults);
        }
        return typedQuery.getResultList();
    }

    public void incrementViewCount(LearningObject learningObject) {
        synchronized (lock) {
            getEntityManager()
                    .createQuery(
                            "update LearningObject lo set lo.views = lo.views + 1, lo.lastInteraction = CURRENT_TIMESTAMP "
                                    + "where lo.id = :id AND lo.deleted = false")
                    .setParameter("id", learningObject.getId()).executeUpdate();
        }
    }

    @Override
    public LearningObject createOrUpdate(LearningObject learningObject) {
        learningObject.setLastInteraction(now());
        return super.createOrUpdate(learningObject);
    }
}
