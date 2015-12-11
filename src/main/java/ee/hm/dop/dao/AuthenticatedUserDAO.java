package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;

public class AuthenticatedUserDAO {

    @Inject
    private EntityManager entityManager;

    public AuthenticatedUser createAuthenticatedUser(AuthenticatedUser authenticatedUser)
            throws DuplicateTokenException {
        AuthenticatedUser merged;
        try {
            merged = entityManager.merge(authenticatedUser);
            entityManager.persist(merged);
        } catch (PersistenceException e) {
            throw new DuplicateTokenException("Duplicate token found when persisting authenticatedUser.");
        }

        return merged;
    }

    public AuthenticatedUser findAuthenticatedUserByToken(String token) {
        TypedQuery<AuthenticatedUser> findByToken = entityManager.createQuery(
                "SELECT a FROM AuthenticatedUser a WHERE a.token = :token", AuthenticatedUser.class);

        AuthenticatedUser user = null;
        try {
            user = findByToken.setParameter("token", token).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return user;
    }

    public void delete(AuthenticatedUser authenticatedUser) {
        AuthenticatedUser merged = entityManager.merge(authenticatedUser);
        entityManager.remove(merged);
    }
}
