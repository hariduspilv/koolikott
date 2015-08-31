package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.User;

public class UserDAO {

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

    public Long countUsersWithSameFullName(String name, String surname) {
        TypedQuery<Long> countByFullName = entityManager.createQuery(
                "SELECT COUNT(u.id) FROM User u WHERE u.name = :name AND u.surname = :surname", Long.class);

        countByFullName.setParameter("name", name).setParameter("surname", surname);

        Long count = 0L;
        try {
            count = countByFullName.getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return count;
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
}
