package ee.hm.dop.utils;

public class NumberUtils {

    public static Long nvl(Long value, Long defaultValue){
        return value != null ? value : defaultValue;
    }

    public static int zvl(int value, int defaultValue){
        return value != 0 ? value : defaultValue;
    }
}
