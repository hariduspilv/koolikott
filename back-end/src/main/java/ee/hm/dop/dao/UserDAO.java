package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.User;

public class UserDAO extends BaseDAO {

    @Inject
    private EntityManager entityManager;

    public User findUserByIdCode(String idCode) {
        TypedQuery<User> findByIdCode = entityManager.createQuery("SELECT u FROM User u WHERE u.idCode = :idCode",
                User.class);

        User user = null;
        try {
            user = findByIdCode.setParameter("idCode", idCode).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return user;
    }

    public User findUserByUsername(String username) {
        TypedQuery<User> findByUsername = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);

        User user = null;
        try {
            user = findByUsername.setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return user;
    }

    /**
     * Counts the amount of users who have the same username, excluding the
     * number at the end. For example, users <i>john.smith</i> and
     * <i>john.smith2</i> are considered to have the same username.
     *
     * @param username the username to search for
     * @return the count of users with the same username, excluding the number
     */
    public Long countUsersWithSameUsername(String username) {
        TypedQuery<User> findByUsername = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username LIKE :username", User.class);

        List<User> users = findByUsername.setParameter("username", username + "%").getResultList();

        return users.stream() //
                .filter(user -> user.getUsername().matches(username + "\\d*")) //
                .count();
    }

    public User update(User user) {
        User merged;
        merged = entityManager.merge(user);
        entityManager.persist(merged);
        return merged;
    }

    public void delete(User user) {
        entityManager.remove(user);
    }

    public List<User> getUsersByRole(Role role) {
        TypedQuery<User> findByRole = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.role = :role", User.class);

        List<User> users = null;
        try {
            users = findByRole.setParameter("role", role).getResultList();
        } catch (Exception e) {
            // ignore
        }

        return users;
    }

    public List<User> getAll() {
        return (List<User>) createQuery("FROM User", User.class)
                .getResultList();
    }
}
