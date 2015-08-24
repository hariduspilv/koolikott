package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;

/**
 * Created by mart.laus on 13.08.2015.
 */
public class AuthenticatedUserDAO {
    @Inject
    private EntityManager entityManager;

    public AuthenticatedUser findAuthenticatedUserByIdCode(String idCode) {
        TypedQuery<AuthenticatedUser> findByIdCode = entityManager.createQuery(
                "SELECT a FROM AuthenticatedUser a WHERE a.user.idCode = :idCode", AuthenticatedUser.class);

        AuthenticatedUser user = null;
        try {
            user = findByIdCode.setParameter("idCode", idCode).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return user;
    }

    public void createAuthenticatedUser(AuthenticatedUser authenticatedUser) throws DuplicateTokenException {
        try {
            entityManager.persist(authenticatedUser);
        } catch (PersistenceException e) {
            throw new DuplicateTokenException("Duplicate token found when persisting authenticatedUser.");
        }
    }
}
