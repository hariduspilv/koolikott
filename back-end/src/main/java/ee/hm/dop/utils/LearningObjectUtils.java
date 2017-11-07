package ee.hm.dop.utils;

public class LearningObjectUtils {
    public static boolean isMaterial(String type) {
        return ".Material".equals(type) || ".ReducedMaterial".equals(type);
    }

    public static boolean isPortfolio(String type) {
        return ".Portfolio".equals(type) || ".ReducedPortfolio".equals(type);
    }
}
