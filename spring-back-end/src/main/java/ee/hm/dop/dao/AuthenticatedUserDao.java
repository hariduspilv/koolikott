package ee.hm.dop.dao;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Date;

@Repository
public class AuthenticatedUserDao extends AbstractDao<AuthenticatedUser> {

    public AuthenticatedUser createAuthenticatedUser(AuthenticatedUser authenticatedUser) throws DuplicateTokenException {
        try {
            return createOrUpdate(authenticatedUser);
        } catch (PersistenceException e) {
            throw new DuplicateTokenException("Duplicate token found when persisting authenticatedUser.");
        }
    }

    public AuthenticatedUser findAuthenticatedUserByToken(String token) {
        return findByField("token", token, "deleted", false);
    }

    public void delete(AuthenticatedUser authenticatedUser) {
        authenticatedUser.setDeleted(true);
        createOrUpdate(authenticatedUser);
    }

    public int deleteOlderThan(LocalDateTime dateTime) {
        return getEntityManager()
                .createNativeQuery("" +
                        "UPDATE AuthenticatedUser " +
                        "SET deleted = 1 " +
                        "WHERE sessionTime < :deleteTime " +
                        "AND deleted = 0", entity())
                .setParameter("deleteTime", Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .executeUpdate();
    }

    public long findCountOfOlderThan(LocalDateTime sessionTime) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("" +
                        "SELECT count(id) from AuthenticatedUser " +
                        "WHERE sessionTime < :sessionTime" +
                        " AND deleted = 0")
                .setParameter("sessionTime", Date.from(sessionTime.atZone(ZoneId.systemDefault()).toInstant()))
                .getSingleResult())
                .longValue();
    }
}
