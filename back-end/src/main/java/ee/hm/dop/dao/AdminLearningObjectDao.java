package ee.hm.dop.dao;

import ee.hm.dop.model.AdminLearningObject;

import java.util.List;

public class AdminLearningObjectDao extends AbstractDao<AdminLearningObject> {

    public List<AdminLearningObject> findByIdDeleted() {
        return findByFieldList("deleted", true);
    }

    public Long findCountByIdDeleted() {
        return (Long) getEntityManager().createQuery("select count(lo) from AdminLearningObject lo where lo.deleted = true")
                .getSingleResult();
    }
}
