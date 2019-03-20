package ee.hm.dop.dao;

import ee.hm.dop.model.User;
import ee.hm.dop.model.UserProfile;
import org.springframework.stereotype.Repository;

@Repository
public class UserProfileDao extends AbstractDao<UserProfile> {

    public UserProfile findByUser(User user) {
        return findByField("user", user);
    }
}
