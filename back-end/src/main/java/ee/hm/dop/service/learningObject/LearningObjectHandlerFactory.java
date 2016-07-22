package ee.hm.dop.service.learningObject;

import java.util.HashMap;
import java.util.Map;

import ee.hm.dop.guice.GuiceInjector;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.PortfolioService;

public class LearningObjectHandlerFactory {

    private static Map<Class<? extends LearningObject>, Class<? extends LearningObjectHandler>> map = new HashMap<>();

    static {
        register(MaterialService.class, Material.class);
        register(PortfolioService.class, Portfolio.class);
    }

    private static void register(Class<? extends LearningObjectHandler> learningObjectHandlerClass,
            Class<? extends LearningObject> learningObjectClass) {
        map.put(learningObjectClass, learningObjectHandlerClass);
    }

    public static LearningObjectHandler get(Class<? extends LearningObject> clazz) {
        return GuiceInjector.getInjector().getInstance(map.get(clazz));
    }
}