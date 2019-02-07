package ee.hm.dop.dao;

import ee.hm.dop.model.User;
import ee.hm.dop.model.UserEmail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserEmailDao extends AbstractDao<UserEmail> {

    public UserEmail findByUser(User user) {
        return findByField("user", user);
    }

    public UserEmail findByUserId(User user) {
        if (user == null) return null;
        if (user.getId() == null) return null;
        List<UserEmail> emails = (List<UserEmail>) getEntityManager().createNativeQuery(
                "SELECT * FROM UserEmail e " +
                        "where e.user = :user", entity())
                .setParameter("user", user.getId())
                .getResultList();
        return !emails.isEmpty() ? emails.get(0) : null;
    }

    public UserEmail findByEmail(String email) {
        return findByField("email", email);
    }
}
