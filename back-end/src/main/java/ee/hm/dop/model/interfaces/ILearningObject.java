package ee.hm.dop.model.interfaces;

import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Visibility;

/**
 * a way to unify LearningObject.class and ReducedLearningObject.class
 */
public interface ILearningObject {
    boolean isDeleted();

    User getCreator();

    Visibility getVisibility();
}
