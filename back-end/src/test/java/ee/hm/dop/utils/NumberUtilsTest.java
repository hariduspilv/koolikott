package ee.hm.dop.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumberUtilsTest {

    private static final long DEFAULT_VALUE_LONG = 10L;
    private static final long VALUE_LONG = 100L;
    private static final int DEFAULT_VALUE_INT = 10;
    private static final int VALUE_INT = 100;

    @Test
    public void nvl_returns_default_value_if_value_is_not_defined() throws Exception {
        assertEquals("Value", DEFAULT_VALUE_LONG, (long) NumberUtils.nvl(null, DEFAULT_VALUE_LONG));
    }

    @Test
    public void nvl_returns_value_if_it_exists() throws Exception {
        assertEquals("Value", VALUE_LONG, (long) NumberUtils.nvl(VALUE_LONG, DEFAULT_VALUE_LONG));
    }

    @Test
    public void zvl_returns_default_value_if_value_is_not_defined() throws Exception {
        assertEquals("Value", DEFAULT_VALUE_INT, NumberUtils.zvl(0, DEFAULT_VALUE_INT));
    }

    @Test
    public void zvl_returns_value_if_it_exists() throws Exception {
        assertEquals("Value", VALUE_INT, NumberUtils.zvl(VALUE_INT, DEFAULT_VALUE_INT));
    }
}