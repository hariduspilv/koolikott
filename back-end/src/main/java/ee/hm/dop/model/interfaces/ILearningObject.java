package ee.hm.dop.model.interfaces;

import ee.hm.dop.model.User;
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
import ee.hm.dop.model.AbstractEntity;
>>>>>>> new-develop
import ee.hm.dop.model.enums.Visibility;
>>>>>>> new-develop

/**
 * a way to unify LearningObject.class and ReducedLearningObject.class
 */
public interface ILearningObject extends AbstractEntity {
    boolean isDeleted();

    User getCreator();
<<<<<<< HEAD
=======

    Visibility getVisibility();
>>>>>>> new-develop
}
