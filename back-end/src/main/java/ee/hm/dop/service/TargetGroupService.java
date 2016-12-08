package ee.hm.dop.service;

import com.google.inject.Inject;
import ee.hm.dop.dao.TargetGroupDAO;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.TargetGroupEnum;
import org.apache.commons.lang3.Range;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TargetGroupService {

    @Inject
    private TargetGroupDAO targetGroupDAO;

    public List<TargetGroup> getValues() {
        return targetGroupDAO.getAll();
    }

    public TargetGroup getByName(String name) {
        return targetGroupDAO.getByName(name);
    }

    public Set<TargetGroup> getTargetGroupsByAge(int from, int to) {
        Set<TargetGroup> targetGroups = new HashSet<>();
        Range<Integer> range = Range.between(from, to);

        for (TargetGroupEnum targetGroupEnum : TargetGroupEnum.values()) {
            if (targetGroupEnum.getRange().isOverlappedBy(range)) {
                targetGroups.add(getByName(targetGroupEnum.name()));
            }
        }

        return targetGroups;
    }
}
