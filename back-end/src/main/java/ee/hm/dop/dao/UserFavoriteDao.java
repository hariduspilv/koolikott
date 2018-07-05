package ee.hm.dop.dao;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserFavoriteDao extends AbstractDao<UserFavorite> {


    public List<Long> returnFavoredLearningObjects(List<Long> idList, User loggedInUser) {
        if (loggedInUser == null || CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        List<BigInteger> favored = getEntityManager().createNativeQuery("" +
                "SELECT\n" +
                "  lo.id \n" +
                "FROM LearningObject lo\n" +
                "  LEFT JOIN (\n" +
                "              SELECT\n" +
                "                uf2.creator,\n" +
                "                uf2.learningObject\n" +
                "              FROM UserFavorite uf2\n" +
                "                JOIN User u ON uf2.creator = u.id\n" +
                "                               AND u.userName = :username\n" +
                "              WHERE uf2.learningObject IN (:idList)\n" +
                "            ) uf ON uf.learningObject = lo.id\n" +
                "WHERE lo.deleted = 0 " +
                "AND lo.id IN (:idList) " +
                "AND uf.creator IS NOT NULL")
                .setParameter("username", loggedInUser.getUsername())
                .setParameter("idList", idList)
                .getResultList();
        return favored.stream().map(BigInteger::longValue).collect(Collectors.toList());
    }

    public UserFavorite findFavoriteByUserAndLearningObject(long id, User user) {
        TypedQuery<UserFavorite> findFavorite = getEntityManager()
                .createQuery("SELECT f FROM UserFavorite f WHERE f.learningObject.id = :loid and f.creator = :uid", entity())
                .setParameter("loid", id)
                .setParameter("uid", user);
        return getSingleResult(findFavorite);
    }

    public void deleteByLearningObjectAndUser(LearningObject learningObject, User user) {
        Query query = getEntityManager().createQuery("DELETE UserFavorite f WHERE f.learningObject = :loid and f.creator = :uid");
        query.setParameter("loid", learningObject);
        query.setParameter("uid", user);
        query.executeUpdate();
    }

    public List<ReducedLearningObject> findUsersFavoritedLearningObjects(User user, int start, int maxResults) {
        String query = "SELECT rlo FROM UserFavorite uf, ReducedLearningObject rlo " +
                "WHERE uf.creator = :creator and uf.learningObject.deleted = false and uf.learningObject.id = rlo.id " +
                "order by uf.learningObject.visibility asc, uf.learningObject.added desc, uf.learningObject.id desc";
        return getEntityManager().createQuery(query, ReducedLearningObject.class)
                .setParameter("creator", user)
                .setFirstResult(start)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public long findUsersFavoritedLearningObjectsCount(User loggedInUser) {
        return (long) entityManager.createQuery("SELECT Count(uf.id) FROM UserFavorite uf " +
                "WHERE uf.creator = :creator and uf.learningObject.deleted = false")
                .setParameter("creator", loggedInUser)
                .getSingleResult();
    }
}
