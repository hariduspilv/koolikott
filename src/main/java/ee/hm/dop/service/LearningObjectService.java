package ee.hm.dop.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.guice.GuiceInjector;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

public class LearningObjectService {

    @Inject
    private LearningObjectDAO learningObjectDAO;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = learningObjectDAO.findById(learningObjectId);

        if (!hasAccess(user, learningObject)) {
            learningObject = null;
        }

        return learningObject;
    }

    public boolean hasAccess(User user, LearningObject learningObject) {
        LearningObjectHandler learningObjectHandler = LearningObjectHandlerFactory.get(learningObject.getClass());
        return learningObjectHandler.hasAccess(user, learningObject);
    }

    protected static class LearningObjectHandlerFactory {

        private static Map<Class<? extends LearningObject>, Class<? extends LearningObjectHandler>> map = new HashMap<>();

        public static void register(Class<? extends LearningObjectHandler> learningObjectHandlerClass,
                Class<? extends LearningObject> learningObjectClass) {
            map.put(learningObjectClass, learningObjectHandlerClass);
        }

        public static LearningObjectHandler get(Class<? extends LearningObject> clazz) {
            return GuiceInjector.getInjector().getInstance(map.get(clazz));
        }
    }
}

interface LearningObjectHandler {

    public boolean hasAccess(User user, LearningObject learningObject);
}
