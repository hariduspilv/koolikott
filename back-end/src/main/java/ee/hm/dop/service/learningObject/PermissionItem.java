package ee.hm.dop.service.learningObject;

import ee.hm.dop.model.User;
import ee.hm.dop.model.interfaces.ILearningObject;

public interface PermissionItem {

    boolean canView(User user, ILearningObject learningObject);

    boolean canAccess(User user, ILearningObject learningObject);

    boolean canUpdate(User user, ILearningObject learningObject);

    boolean isPublic(ILearningObject learningObject);
}