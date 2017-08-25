package ee.hm.dop.dao;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class UserFavoriteDao extends AbstractDao<UserFavorite> {

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
        String query = "SELECT rlo FROM UserFavorite uf, ReducedLearningObject rlo WHERE uf.creator = :creator and uf.learningObject.deleted = false and uf.learningObject.id = rlo.id order by uf.learningObject.added desc";
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
