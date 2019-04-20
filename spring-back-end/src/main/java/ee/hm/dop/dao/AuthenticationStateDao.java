package ee.hm.dop.dao;

import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Date;

@Repository
public class AuthenticationStateDao extends AbstractDao<AuthenticationState> {

    public int deleteOlderThan(LocalDateTime dateTime) {
        return getEntityManager()
                .createNativeQuery("DELETE FROM AuthenticationState WHERE created < :deleteTime")
                .setParameter("deleteTime", Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .executeUpdate();
    }

    public long findCountOfOlderThan(LocalDateTime dateTime) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(id) from AuthenticationState WHERE created < :deleteTime")
                .setParameter("deleteTime", Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .getSingleResult())
                .longValue();
    }

    public AuthenticationState createAuthenticationState(AuthenticationState authenticationState) throws DuplicateTokenException {
        try {
            return createOrUpdate(authenticationState);
        } catch (PersistenceException e) {
            throw new DuplicateTokenException("Duplicate token found when persisting authentication state.",e);
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
