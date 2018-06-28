package ee.hm.dop.dao;

import ee.hm.dop.model.*;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.Query;
import javax.persistence.SqlResultSetMapping;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReducedLearningObjectDao extends AbstractDao<ReducedLearningObject> {


    public List<ReducedLearningObject> findMaterialByCreator(User creator, int start, int maxResults) {
        Query query = getEntityManager()
                .createQuery("FROM ReducedMaterial rlo" +
                        " WHERE rlo.creator.id = :creatorId " +
                        "AND rlo.deleted = false order by added desc", ReducedMaterial.class);
        return findByCreator(query, creator, start, maxResults);
    }

    public List<ReducedLearningObject> findPortfolioByCreator(User creator, int start, int maxResults) {
        Query query = getEntityManager()
                .createQuery("FROM ReducedPortfolio rlo " +
                        "WHERE rlo.creator.id = :creatorId " +
                        "AND rlo.deleted = false order by added desc", ReducedPortfolio.class);
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
