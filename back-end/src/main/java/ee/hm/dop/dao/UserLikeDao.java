package ee.hm.dop.dao;

import ee.hm.dop.model.*;
import org.joda.time.DateTime;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class UserLikeDao extends AbstractDao<UserLike> {

    public UserLike findPortfolioUserLike(Portfolio portfolio, User user) {
        return findByLearningObjectAndUser(portfolio, user);
    }

    public UserLike findMaterialUserLike(Material material, User user) {
        return findByLearningObjectAndUser(material, user);
    }

    private UserLike findByLearningObjectAndUser(LearningObject learningObject, User user) {
        TypedQuery<UserLike> findLike = getEntityManager().createQuery(
                "SELECT ul FROM UserLike ul WHERE ul.learningObject = :loid and ul.creator = :uid", UserLike.class) //
                .setParameter("loid", learningObject) //
                .setParameter("uid", user);

        return getSingleResult(findLike);
    }

    public void deletePortfolioLike(Portfolio portfolio, User user) {
        deleteByLearningObjectAndUser(portfolio, user);
    }

    public void deleteMaterialLike(Material material, User user) {
        deleteByLearningObjectAndUser(material, user);
    }

    private void deleteByLearningObjectAndUser(LearningObject learningObject, User user) {
        getEntityManager()
                .createQuery("DELETE UserLike ul WHERE ul.learningObject = :loid and ul.creator = :uid")
                .setParameter("loid", learningObject)
                .setParameter("uid", user)
                .executeUpdate();
    }

    public UserLike update(UserLike userLike) {
        if (userLike.getId() == null) {
            userLike.setAdded(DateTime.now());
        }
        return createOrUpdate(userLike);
    }

    public List<Searchable> findMostLikedSince(DateTime date, int numberOfMaterials) {
        List<Object[]> resultList = getEntityManager().createQuery("SELECT ul.learningObject, 2 * SUM(ul.isLiked) - COUNT(*) AS score" //
                + " FROM UserLike ul" //
                + " WHERE ul.added > :from AND ul.learningObject.deleted = false" //
                + " GROUP BY ul.learningObject" //
                + " HAVING (2 * SUM(ul.isLiked) - COUNT(*)) > 0" //
                + " ORDER BY score DESC", Object[].class) //
                .setParameter("from", date) //
                .setMaxResults(numberOfMaterials) //
                .getResultList();
        return resultList.stream().map(result -> (Searchable) result[0]).collect(Collectors.toList());
    }
}
