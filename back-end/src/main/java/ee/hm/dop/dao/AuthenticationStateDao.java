package ee.hm.dop.dao;

import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import org.joda.time.DateTime;

import javax.persistence.PersistenceException;
import java.math.BigInteger;

public class AuthenticationStateDao extends AbstractDao<AuthenticationState> {

    public int deleteOlderThan(DateTime dateTime) {
        return getEntityManager()
                .createNativeQuery("DELETE FROM AuthenticationState WHERE created < :deleteTime")
                .setParameter("deleteTime", dateTime.toDate())
                .executeUpdate();
    }

    public long findCountOfOlderThan(DateTime dateTime) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(id) from AuthenticationState WHERE created < :deleteTime")
                .setParameter("deleteTime", dateTime.toDate())
                .getSingleResult())
                .longValue();
    }

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
