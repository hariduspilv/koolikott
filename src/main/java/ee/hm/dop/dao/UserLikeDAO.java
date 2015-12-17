package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import ee.hm.dop.model.Portfolio;
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

	public void deletePortfolioLike(Portfolio portfolio, User user) {
		Query query = entityManager.createQuery("DELETE UserLike ul WHERE ul.portfolio = :pid and ul.creator = :uid");
		query.setParameter("pid", portfolio);
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

}
