package ee.hm.dop.dao;

import ee.hm.dop.model.TargetGroup;
import java.util.List;

public class TargetGroupDAO extends BaseDAO<TargetGroup> {

    public List<TargetGroup> getAll() {
        return createQuery("FROM TargetGroup tg", TargetGroup.class).getResultList();
    }

    public TargetGroup getByName(String name) {
        return getSingleResult(createQuery("FROM TargetGroup WHERE name = :name", TargetGroup.class)
                .setParameter("name", name));
    }
}
