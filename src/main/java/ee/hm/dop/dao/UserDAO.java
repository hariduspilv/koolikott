package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import ee.hm.dop.exceptions.DuplicateUserException;
import ee.hm.dop.model.User;

/**
 * Created by mart.laus on 13.08.2015.
 */
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
        TypedQuery<Long> findByFullName = entityManager.createQuery(
                "SELECT COUNT(u.id) FROM User u WHERE u.name = :name AND u.surname = :surname", Long.class);

        findByFullName.setParameter("name", name).setParameter("surname", surname);

        Long count = null;
        try {
            count = findByFullName.getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return count;
    }

    public void createUser(User user) throws DuplicateUserException {
        try {
            entityManager.persist(user);
        } catch (PersistenceException e) {
            throw new DuplicateUserException("Duplicate unique fields found when persisting user. ");
        }
    }
}
