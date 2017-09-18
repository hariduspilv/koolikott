package ee.hm.dop.model.interfaces;

import ee.hm.dop.model.User;

/**
 * a way to unify LearningObject.class and ReducedLearningObject.class
 */
public interface ILearningObject {
    boolean isDeleted();

    User getCreator();
}
