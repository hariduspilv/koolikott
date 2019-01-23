package ee.hm.dop.model.enums;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TargetGroupEnumTest {

    @Test
    public void all_are_matched() {
        List<TargetGroupEnum> enums = TargetGroupEnum.getTargetGroupEnumsByAge(0, 19);
        assertArrayEquals(TargetGroupEnum.values(), enums.toArray());
    }

    @Test
    public void all_are_matched_reverse() {
        List<TargetGroupEnum> enums = TargetGroupEnum.getTargetGroupEnumsByAge(19, 0);
        assertArrayEquals(TargetGroupEnum.values(), enums.toArray());
    }

    @Test
    public void zero_hits_one() {
        List<TargetGroupEnum> enums = TargetGroupEnum.getTargetGroupEnumsByAge(0, 0);
        assertEquals(1, enums.size());
    }
}