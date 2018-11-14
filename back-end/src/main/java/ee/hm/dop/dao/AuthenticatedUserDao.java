package ee.hm.dop.dao;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import org.joda.time.DateTime;

import javax.persistence.PersistenceException;
import java.math.BigInteger;

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

    public int deleteOlderThan(DateTime dateTime) {
        return getEntityManager()
                .createNativeQuery("" +
                        "UPDATE AuthenticatedUser " +
                        "SET deleted = 1 " +
                        "WHERE sessionTime < :deleteTime " +
                        "AND deleted = 0", entity())
                .setParameter("deleteTime", dateTime.toDate())
                .executeUpdate();
    }

    public long findCountOfOlderThan(DateTime sessionTime) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("" +
                        "SELECT count(id) from AuthenticatedUser " +
                        "WHERE sessionTime < :sessionTime" +
                        " AND deleted = 0")
                .setParameter("sessionTime", sessionTime.toDate())
                .getSingleResult())
                .longValue();
    }
}
