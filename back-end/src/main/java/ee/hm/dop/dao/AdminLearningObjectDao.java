package ee.hm.dop.dao;

import ee.hm.dop.model.AdminLearningObject;

import java.util.List;

public class AdminLearningObjectDao extends AbstractDao<AdminLearningObject> {

    public List<AdminLearningObject> findByIdDeleted() {
        return getEntityManager().createQuery("" +
                "select lo from AdminLearningObject lo " +
                "where lo.deleted = true " +
                "and lo.visibility in ('PUBLIC', 'NOT_LISTED')")
                .getResultList();
    }

    public Long findCountByIdDeleted() {
        return (Long) getEntityManager().createQuery("" +
                "select count(lo) from AdminLearningObject lo " +
                "where lo.deleted = true " +
                "and lo.visibility in ('PUBLIC', 'NOT_LISTED')")
                .getSingleResult();
    }

    public List<AdminLearningObject> findLOById(List<Long> list, String ordering) {
        return getEntityManager().createQuery(
                "select lo from AdminLearningObject lo where lo.id in (:list) order by lo.added " + ordering)
                .setParameter("list", list)
                .getResultList();
    }
}
