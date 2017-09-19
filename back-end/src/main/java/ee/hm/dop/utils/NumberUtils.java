package ee.hm.dop.utils;

public class NumberUtils {

    public static Long nvl(Long value, Long defaultValue){
        return value != null ? value : defaultValue;
    }
}
