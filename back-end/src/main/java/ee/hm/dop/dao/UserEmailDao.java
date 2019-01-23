package ee.hm.dop.dao;

import ee.hm.dop.model.User;
import ee.hm.dop.model.UserEmail;

public class UserEmailDao extends AbstractDao<UserEmail> {

    public UserEmail findByUser(User user) {
        return findByField("user", user);
    }

    public UserEmail findByEmail(String email) {
        return findByField("email", email);
    }
}
