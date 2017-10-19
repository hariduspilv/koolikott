package ee.hm.dop.model.interfaces;

import ee.hm.dop.model.User;
<<<<<<< HEAD
=======
import ee.hm.dop.model.enums.Visibility;
>>>>>>> new-develop

/**
 * a way to unify LearningObject.class and ReducedLearningObject.class
 */
public interface ILearningObject {
    boolean isDeleted();

    User getCreator();
<<<<<<< HEAD
=======

    Visibility getVisibility();
>>>>>>> new-develop
}
