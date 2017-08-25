package ee.hm.dop.dao;

import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.ReducedMaterial;
import ee.hm.dop.model.ReducedPortfolio;
import ee.hm.dop.model.User;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;


public class ReducedLearningObjectDAO extends BaseDAO<ReducedLearningObject> {

    public List<ReducedLearningObject> findMaterialByCreator(User creator, int start, int maxResults) {
        Query query = createQuery("FROM ReducedMaterial rlo WHERE rlo.creator.id = :creatorId AND rlo.deleted = false order by added desc", ReducedMaterial.class);

        List<ReducedLearningObject> learningObjects = findByCreator(query, creator, start, maxResults);
//todo delete this
        removeNot(ReducedMaterial.class, learningObjects);
        return learningObjects;
    }

    public List<ReducedLearningObject> findPortfolioByCreator(User creator, int start, int maxResults) {
        Query query = createQuery("FROM ReducedPortfolio rlo WHERE rlo.creator.id = :creatorId AND rlo.deleted = false order by added desc", ReducedPortfolio.class);

        List<ReducedLearningObject> learningObjects = findByCreator(query, creator, start, maxResults);
//todo delete this
        removeNot(ReducedPortfolio.class, learningObjects);

        return learningObjects;
    }

    private List<ReducedLearningObject> findByCreator(Query query, User creator, int start, int maxResults) {
        query.setParameter("creatorId", creator.getId()).setFirstResult(start);
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    public List<ReducedLearningObject> findAllById(List<Long> idList) {
        TypedQuery<ReducedLearningObject> findAllByIdList = createQuery(
                "SELECT rlo FROM ReducedLearningObject rlo WHERE rlo.deleted = false AND rlo.id in :idList", ReducedLearningObject.class);
        return findAllByIdList.setParameter("idList", idList).getResultList();
    }

    private <T> void removeNot(Class<T> clazz, List<ReducedLearningObject> learningObjects) {
        learningObjects.removeIf(learningObject -> learningObject.getClass() != clazz);
    }
}
