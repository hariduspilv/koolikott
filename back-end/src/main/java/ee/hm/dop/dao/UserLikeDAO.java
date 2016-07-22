package ee.hm.dop.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import org.joda.time.DateTime;

public class UserLikeDAO extends BaseDAO<UserLike> {

    public UserLike findPortfolioUserLike(Portfolio portfolio, User user) {
        return findByLearningObjectAndUser(portfolio, user);
    }

    public UserLike findMaterialUserLike(Material material, User user) {
        return findByLearningObjectAndUser(material, user);
    }

    private UserLike findByLearningObjectAndUser(LearningObject learningObject, User user) {
        TypedQuery<UserLike> findLike = createQuery(
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
        Query query = getEntityManager()
                .createQuery("DELETE UserLike ul WHERE ul.learningObject = :loid and ul.creator = :uid");
        query.setParameter("loid", learningObject);
        query.setParameter("uid", user);
        query.executeUpdate();
    }

    @Override
    public UserLike update(UserLike userLike) {
        if (userLike.getId() == null) {
            userLike.setAdded(DateTime.now());
        }

        return super.update(userLike);
    }

    public List<Searchable> findMostLikedSince(DateTime date, int numberOfMaterials) {
        List<Object[]> resultList = createQuery("SELECT ul.learningObject, 2 * SUM(ul.isLiked) - COUNT(*) AS score" //
                + " FROM UserLike ul" //
                + " WHERE ul.added > :from AND ul.learningObject.deleted = false" //
                + " GROUP BY ul.learningObject" //
                + " HAVING (2 * SUM(ul.isLiked) - COUNT(*)) > 0" //
                + " ORDER BY score DESC", Object[].class) //
                        .setParameter("from", date) //
                        .setMaxResults(numberOfMaterials) //
                        .getResultList();

        List<Searchable> results = new ArrayList<>();

        for (Object[] result : resultList) {
            results.add((Searchable) result[0]);
        }

        return results;
    }
}
