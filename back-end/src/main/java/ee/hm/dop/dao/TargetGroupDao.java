package ee.hm.dop.dao;

import ee.hm.dop.model.TargetGroup;

public class TargetGroupDao extends AbstractDao<TargetGroup> {

    public TargetGroup getByName(String name) {
        return findByFieldLowerCase("name", name);
    }
}
