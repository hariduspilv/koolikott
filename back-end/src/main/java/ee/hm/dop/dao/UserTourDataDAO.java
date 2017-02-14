package ee.hm.dop.dao;


import ee.hm.dop.model.User;
import ee.hm.dop.model.UserTourData;

import javax.persistence.NoResultException;

public class UserTourDataDAO extends BaseDAO<UserTourData> {

    public UserTourData getUserTourData(User user) {
        try {
            return createQuery("FROM UserTourData udt WHERE udt.user.id=:userId", UserTourData.class)
                    .setParameter("userId", user.getId())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserTourData addUserTourData(UserTourData userTourData) {
        return update(userTourData);
    }
}
