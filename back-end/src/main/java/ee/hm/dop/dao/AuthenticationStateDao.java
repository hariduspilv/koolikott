package ee.hm.dop.dao;

import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;

import javax.persistence.PersistenceException;

public class AuthenticationStateDao extends AbstractDao<AuthenticationState> {

    public AuthenticationState createAuthenticationState(AuthenticationState authenticationState) throws DuplicateTokenException {
        try {
            return createOrUpdate(authenticationState);
        } catch (PersistenceException e) {
            throw new DuplicateTokenException("Duplicate token found when persisting authentication state.");
        }
    }

    public AuthenticationState findAuthenticationStateByToken(String token) {
        return findByField("token", token);
    }

    public void delete(AuthenticationState authenticationState) {
        AuthenticationState merged = entityManager.merge(authenticationState);
        entityManager.remove(merged);
    }
}
