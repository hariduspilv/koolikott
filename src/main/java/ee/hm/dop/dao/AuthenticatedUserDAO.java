package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;

/**
 * Created by mart.laus on 13.08.2015.
 */
public class AuthenticatedUserDAO {
    @Inject
    private EntityManager entityManager;

    public void createAuthenticatedUser(AuthenticatedUser authenticatedUser) throws DuplicateTokenException {
        try {
            entityManager.persist(authenticatedUser);
        } catch (PersistenceException e) {
            throw new DuplicateTokenException("Duplicate token found when persisting authenticatedUser.");
        }
    }
}
