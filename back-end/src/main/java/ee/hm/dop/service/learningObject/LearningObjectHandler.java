package ee.hm.dop.service.learningObject;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

public interface LearningObjectHandler {

    boolean hasAccess(User user, LearningObject learningObject);

    boolean isPublic(LearningObject learningObject);
}