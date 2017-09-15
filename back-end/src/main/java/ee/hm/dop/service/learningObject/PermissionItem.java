package ee.hm.dop.service.learningObject;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

public interface PermissionItem {

    boolean canAccess(User user, LearningObject learningObject);

    boolean canUpdate(User user, LearningObject learningObject);

    boolean isPublic(LearningObject learningObject);
}