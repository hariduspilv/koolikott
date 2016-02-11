package ee.hm.dop.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;

public class UserLikeDAO {

    @Inject
    private EntityManager entityManager;

    public UserLike findPortfolioUserLike(Portfolio portfolio, User user) {
        TypedQuery<UserLike> findLike = entityManager.createQuery(
                "SELECT ul FROM UserLike ul WHERE ul.portfolio = :pid and ul.creator = :uid", UserLike.class);

        UserLike like = null;
        try {
            findLike.setParameter("pid", portfolio);
            findLike.setParameter("uid", user);
            like = findLike.getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return like;
    }

    public UserLike findMaterialUserLike(Material material, User user) {
        TypedQuery<UserLike> findLike = entityManager.createQuery(
                "SELECT ul FROM UserLike ul WHERE ul.material = :mid and ul.creator = :uid", UserLike.class);

        UserLike like = null;
        try {
            findLike.setParameter("mid", material);
            findLike.setParameter("uid", user);
            like = findLike.getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return like;
    }

    public void deletePortfolioLike(Portfolio portfolio, User user) {
        Query query = entityManager.createQuery("DELETE UserLike ul WHERE ul.portfolio = :pid and ul.creator = :uid");
        query.setParameter("pid", portfolio);
        query.setParameter("uid", user);
        query.executeUpdate();
    }

    public void deleteMaterialLike(Material material, User user) {
        Query query = entityManager.createQuery("DELETE UserLike ul WHERE ul.material = :mid and ul.creator = :uid");
        query.setParameter("mid", material);
        query.setParameter("uid", user);
        query.executeUpdate();
    }

    public UserLike update(UserLike userLike) {
        if (userLike.getId() == null) {
            userLike.setAdded(DateTime.now());
        }
        UserLike merged = entityManager.merge(userLike);
        entityManager.persist(merged);
        return merged;
    }

    public List<Searchable> findMostLikedSince(DateTime date, int numberOfMaterials) {
        int MATERIAL_SELECT_INDEX = 0;
        int PORFOLIO_SELECT_INDEX = 1;

        List<Object[]> resultList = entityManager
                .createQuery("SELECT ul.material, ul.portfolio, 2 * SUM(ul.isLiked) - COUNT(*) AS score" //
                        + " FROM UserLike ul" //
                        + " LEFT JOIN ul.material" //
                        + " LEFT JOIN ul.portfolio" //
                        + " WHERE ul.added > :from" //
                        + " GROUP BY ul.material, ul.portfolio" //
                        + " ORDER BY score DESC", Object[].class) //
                .setParameter("from", date) //
                .setMaxResults(numberOfMaterials) //
                .getResultList();

        List<Searchable> results = new ArrayList<>();

        for (Object[] result : resultList) {
            if (result[MATERIAL_SELECT_INDEX] != null) {
                results.add((Searchable) result[MATERIAL_SELECT_INDEX]);
            } else if (result[PORFOLIO_SELECT_INDEX] != null) {
                results.add((Searchable) result[PORFOLIO_SELECT_INDEX]);
            }
        }

        return results;
    }
}
