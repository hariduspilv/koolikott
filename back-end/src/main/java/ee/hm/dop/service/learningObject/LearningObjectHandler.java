package ee.hm.dop.service.learningObject;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

public interface LearningObjectHandler {

    boolean hasPermissionsToAccess(User user, LearningObject learningObject);

    boolean hasPermissionsToUpdate(User user, LearningObject learningObject);

    boolean isPublic(LearningObject learningObject);
}