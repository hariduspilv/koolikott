package ee.hm.dop.service.synchronizer;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MergeUtil {

    private static final Logger logger = LoggerFactory.getLogger(MergeUtil.class);

    /**
     * Data from the source will be copied to the destination.
     * If the source data is not null or empty, then existing data in the destination will be overwritten
     *
     * @param source Data being copied to the destination
     * @param dest   Object into which data will be copied and overwritten
     */
    public static void mergeTwoObjects(Object source, Object dest) {
        try {
            new BeanUtilsBean() {
                @Override
                public void copyProperty(Object dest, String name, Object value)
                        throws IllegalAccessException, InvocationTargetException {
                    if (isNotEmpty(value)) {
                        super.copyProperty(dest, name, value);
                    }
                }
            }.copyProperties(dest, source);
        } catch (Exception e) {
            logger.error("Unable to merge existing material and downloaded material from the repository", e);
        }
    }

    private static boolean isNotEmpty(Object value) {
        return value != null && !isEmpty(value);
    }

    private static boolean isEmpty(Object value) {
        if (value instanceof List) {
            return ((List) value).isEmpty();
        } else {
            return value instanceof String && ((String) value).isEmpty();
        }
    }
}
