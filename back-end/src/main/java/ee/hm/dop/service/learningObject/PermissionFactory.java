package ee.hm.dop.service.learningObject;

import java.util.HashMap;
import java.util.Map;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.PortfolioService;

public class PermissionFactory {

    private static Map<Class<? extends LearningObject>, Class<? extends PermissionItem>> map = new HashMap<>();

    static {
        register(MaterialService.class, Material.class);
        register(PortfolioService.class, Portfolio.class);
    }

    private static void register(Class<? extends PermissionItem> learningObjectHandlerClass, Class<? extends LearningObject> learningObjectClass) {
        map.put(learningObjectClass, learningObjectHandlerClass);
    }

    public static PermissionItem get(Class<? extends LearningObject> clazz) {
        return GuiceInjector.getInjector().getInstance(map.get(clazz));
    }
}