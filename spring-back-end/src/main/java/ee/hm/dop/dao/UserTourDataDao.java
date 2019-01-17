package ee.hm.dop.dao;


import ee.hm.dop.model.User;
import ee.hm.dop.model.UserTourData;

import javax.persistence.NoResultException;

public class UserTourDataDao extends AbstractDao<UserTourData> {

    public UserTourData getUserTourData(User user) {
            return getSingleResult(getEntityManager()
                    .createQuery("FROM UserTourData udt WHERE udt.user.id=:userId", entity())
                    .setParameter("userId", user.getId()));
    }
}
