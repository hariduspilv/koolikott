package ee.hm.dop.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.User;

public class UserDao extends AbstractDao<User> {

    public User findUserByIdCode(String idCode) {
        return findByField("idCode", idCode);
    }

    public User findUserByUsername(String username) {
        return findByField("username", username);
    }

    /**
     * Counts the amount of users who have the same username, excluding the
     * number at the end. For example, users <i>john.smith</i> and
     * <i>john.smith2</i> are considered to have the same username.
     *username
     * @param username the username to search for
     * @return the count of users with the same username, excluding the number
     */
    public Long countUsersWithSameUsername(String username) {
        BigInteger count = (BigInteger) entityManager.createNativeQuery("select count(userName) from User where userName REGEXP :username")
                .setParameter("username","\\b"+ username + "[0-9]*$\\b")
                .getSingleResult();
        return count.longValue();
    }

    public void delete(User user) {
        entityManager.remove(user);
    }

    public List<User> getUsersByRole(Role role) {
        return findByFieldList("role", role);
    }

    public BigDecimal getUsersCountByRole(Role role) {
        return (BigDecimal) getCountByField("role", role);
    }
}
