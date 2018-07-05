package ee.hm.dop.dao;

import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.ReducedMaterial;
import ee.hm.dop.model.ReducedPortfolio;
import ee.hm.dop.model.User;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class ReducedLearningObjectDao extends AbstractDao<ReducedLearningObject> {


    public List<ReducedLearningObject> findMaterialByCreator(User creator, int start, int maxResults) {
        Query query = getEntityManager()
                .createQuery("select rlo FROM ReducedMaterial rlo " +
                        "left outer join rlo.userFavorites as uf on uf.creator.id = :creatorId " +
                        "WHERE rlo.creator.id = :creatorId AND rlo.deleted = false " +
                        "order by rlo.visibility asc, uf.creator.id desc, rlo.added desc", ReducedMaterial.class);
        return findByCreator(query, creator, start, maxResults);
    }

    public List<ReducedLearningObject> findPortfolioByCreator(User creator, int start, int maxResults) {
        Query query = getEntityManager()
                .createQuery("select rlo FROM ReducedPortfolio rlo " +
                        "left outer join rlo.userFavorites as uf on uf.creator.id = :creatorId " +
                        "WHERE rlo.creator.id = :creatorId AND rlo.deleted = false " +
                        "order by rlo.visibility asc, uf.creator.id desc, rlo.added desc", ReducedPortfolio.class);
        return findByCreator(query, creator, start, maxResults);
    }

    private List<ReducedLearningObject> findByCreator(Query query, User creator, int start, int maxResults) {
        query.setParameter("creatorId", creator.getId()).setFirstResult(start);
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    public List<ReducedLearningObject> findAllById(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        List<ReducedLearningObject> reducedLearningObjects = getEntityManager()
                .createQuery("SELECT rlo FROM ReducedLearningObject rlo " +
                        "WHERE rlo.deleted = false " +
                        "AND rlo.id in :idList", entity())
                .setParameter("idList", idList)
                .getResultList();
        return reducedLearningObjects;
    }
}
