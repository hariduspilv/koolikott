package ee.hm.dop.dao;

import ee.hm.dop.model.User;
import ee.hm.dop.model.UserProfile;

public class UserProfileDao extends AbstractDao<UserProfile> {

    public UserProfile findByUser(User user) {
        return findByField("user", user);
    }
}
