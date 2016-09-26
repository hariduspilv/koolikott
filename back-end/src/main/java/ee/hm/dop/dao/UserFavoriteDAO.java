package ee.hm.dop.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;

public class UserFavoriteDAO extends BaseDAO<UserFavorite> {

    @Override
    public UserFavorite update(UserFavorite userFavorite) {
        return super.update(userFavorite);
    }

    public UserFavorite findFavoriteByUserAndLearningObject(LearningObject learningObject, User user) {
        TypedQuery<UserFavorite> findFavorite = createQuery("SELECT f FROM UserFavorite f WHERE f.learningObject = :loid and f.creator = :uid", UserFavorite.class)
                .setParameter("loid", learningObject)
                .setParameter("uid", user);

        return getSingleResult(findFavorite);
    }

    public void deleteByLearningObjectAndUser(LearningObject learningObject, User user) {
        Query query = getEntityManager().createQuery("DELETE UserFavorite f WHERE f.learningObject = :loid and f.creator = :uid");
        query.setParameter("loid", learningObject);
        query.setParameter("uid", user);
        query.executeUpdate();
    }

    public List<LearningObject> findUsersFavoritedLearningObjects(User user) {
        String query = "SELECT uf.learningObject FROM UserFavorite uf WHERE uf.creator = :creator and uf.learningObject.deleted = false order by uf.learningObject.added desc";
        TypedQuery<LearningObject> findAllByCreator = createQuery(query, LearningObject.class);
        return findAllByCreator.setParameter("creator", user).getResultList();
    }
}
