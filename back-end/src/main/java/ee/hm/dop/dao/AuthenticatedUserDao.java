package ee.hm.dop.dao;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;

import javax.persistence.PersistenceException;

public class AuthenticatedUserDao extends AbstractDao<AuthenticatedUser> {

    public AuthenticatedUser createAuthenticatedUser(AuthenticatedUser authenticatedUser) throws DuplicateTokenException {
        try {
            return createOrUpdate(authenticatedUser);
        } catch (PersistenceException e) {
            throw new DuplicateTokenException("Duplicate token found when persisting authenticatedUser.");
        }
    }

    public AuthenticatedUser findAuthenticatedUserByToken(String token) {
        return super.findByField("token", token);
    }

    public void delete(AuthenticatedUser authenticatedUser) {
        AuthenticatedUser merged = getEntityManager().merge(authenticatedUser);
        getEntityManager().remove(merged);
    }
}
