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

//    public String findByUserId(int userId) {
//        return getEntityManager()
//                .createQuery(
//                        "select e.email from UserEmail ue left join User u on ue.user = u.id " +
//                                "where ue.user = :userId", entity())
//                .setParameter("userId", userId)
//                .getSingleResult();
//    }
}
