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

public class UserFavoriteDAO extends BaseDAO<UserFavorite> {
    @Inject
    private EntityManager entityManager;

    @Override
    public UserFavorite update(UserFavorite userFavorite) {
        return super.update(userFavorite);
    }

    public UserFavorite findFavoriteByUserAndLearningObject(long id, User user) {
        TypedQuery<UserFavorite> findFavorite =
                createQuery("SELECT f FROM UserFavorite f WHERE f.learningObject.id = :loid and f.creator = :uid", UserFavorite.class)
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
        TypedQuery<ReducedLearningObject> findAllByCreator = createQuery(query, ReducedLearningObject.class);
        return findAllByCreator.setParameter("creator", user).setFirstResult(start).setMaxResults(maxResults).getResultList();
    }

    public long findUsersFavoritedLearningObjectsCount(User loggedInUser) {
        String queryString = "SELECT Count(uf.id) FROM UserFavorite uf WHERE uf.creator = :creator and uf.learningObject.deleted = false";
        Query query = entityManager.createQuery(queryString);
        return (long)query.setParameter("creator", loggedInUser).getSingleResult();
    }
}
