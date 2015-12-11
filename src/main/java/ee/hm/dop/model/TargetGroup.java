package ee.hm.dop.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Range;

public enum TargetGroup {
    ZERO_FIVE(0, 5), SIX_SEVEN(6, 7), GRADE1(7, 8), GRADE2(8, 9), GRADE3(9, 10), GRADE4(10, 11), GRADE5(11, 12), GRADE6(
            12, 13), GRADE7(13, 14), GRADE8(14, 15), GRADE9(15, 16), GYMNASIUM(16, 19);

    private final Range<Integer> range;

    TargetGroup(int from, int to) {
        this.range = Range.between(from, to);
    }

    public static Set<TargetGroup> getTargetGroupsByAge(int from, int to) {
        Set<TargetGroup> targetGroups = new HashSet<>();
        Range<Integer> range = Range.between(from, to);

        for (TargetGroup targetGroup : TargetGroup.values()) {
            if (targetGroup.range.isOverlappedBy(range)) {
                targetGroups.add(targetGroup);
            }
        }

        return targetGroups;
    }
}
