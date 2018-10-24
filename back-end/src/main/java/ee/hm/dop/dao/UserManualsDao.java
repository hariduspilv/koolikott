package ee.hm.dop.dao;

import ee.hm.dop.model.UserManuals;

import java.util.List;

public class UserManualsDao extends AbstractDao<UserManuals> {

    public List<UserManuals> getUserManuals() {
        return getList(entityManager
                .createQuery("select u from UserManuals u " +
                        "order by a.id desc", entity()));
    }

    public void delete(UserManuals userManuals) { entityManager.remove(userManuals);}
}
