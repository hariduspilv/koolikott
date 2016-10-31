package ee.hm.dop.dao;

import static org.joda.time.DateTime.now;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

public class LearningObjectDAO extends BaseDAO<LearningObject> {

    private static final Object lock = new Object();

    public LearningObject findByIdNotDeleted(long objectId) {
        TypedQuery<LearningObject> findByCode = createQuery(
                "SELECT lo FROM LearningObject lo WHERE lo.id = :id AND lo.deleted = false", LearningObject.class) //
                .setParameter("id", objectId);

        return getSingleResult(findByCode);
    }

    public LearningObject findById(long objectId) {
        TypedQuery<LearningObject> findByCode = createQuery("SELECT lo FROM LearningObject lo WHERE lo.id = :id",
                LearningObject.class) //
                .setParameter("id", objectId);

        return getSingleResult(findByCode);
    }

    public List<LearningObject> findDeletedLearningObjects() {
        TypedQuery<LearningObject> query = createQuery("SELECT lo FROM LearningObject lo WHERE lo.deleted = true",
                LearningObject.class);
        return query.getResultList();
    }

    /**
     * finds all LearningObjects contained in the idList. There is no guarantee
     * about in which order the LearningObjects will be in the result list.
     *
     * @param idList the list with LearningObject ids
     * @return a list of LearningObject specified by idList
     */
    public List<LearningObject> findAllById(List<Long> idList) {
        TypedQuery<LearningObject> findAllByIdList = createQuery(
                "SELECT lo FROM LearningObject lo WHERE lo.deleted = false AND lo.id in :idList", LearningObject.class);
        return findAllByIdList.setParameter("idList", idList).getResultList();
    }

    public List<LearningObject> findNewestLearningObjects(int numberOfLearningObjects, int startPosition) {
        return createQuery("FROM LearningObject lo WHERE lo.deleted = false ORDER BY added DESC, id DESC",
                LearningObject.class).setFirstResult(startPosition).setMaxResults(numberOfLearningObjects)
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
        update(learningObject);
    }

    /**
     * Find all LearningObjects with the specified creator. LearningObjects are
     * ordered by added date with newest first.
     *
     * @param creator User who created the LearningObjects
     * @return A list of LearningObject
     */
    public List<LearningObject> findByCreator(User creator, int start, int maxResults) {
        String query = "SELECT lo FROM LearningObject lo WHERE lo.creator.id = :creatorId AND lo.deleted = false order by added desc";
        TypedQuery<LearningObject> findAllByCreator = createQuery(query, LearningObject.class);
        return findAllByCreator.setParameter("creatorId", creator.getId()).setFirstResult(start).setMaxResults(maxResults).getResultList();
    }

    protected <T> void removeNot(Class<T> clazz, List<LearningObject> learningObjects) {
        Iterator<LearningObject> iterator = learningObjects.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getClass() != clazz) {
                iterator.remove();
            }
        }
    }

    protected <T> T castTo(Class<T> clazz, LearningObject learningObject) {
        if (learningObject != null && learningObject.getClass() == clazz) {
            return clazz.cast(learningObject);
        }

        return null;
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
    public LearningObject update(LearningObject learningObject) {
        learningObject.setLastInteraction(now());

        return super.update(learningObject);
    }
}
