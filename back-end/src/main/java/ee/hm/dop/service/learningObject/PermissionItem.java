package ee.hm.dop.service.learningObject;

import ee.hm.dop.model.User;
import ee.hm.dop.model.interfaces.ILearningObject;

public interface PermissionItem {

    /**
     * Something searchable. Public, not listed. For example, you can view a link if someone shares etc.
     */
    default boolean canView(User user, ILearningObject learningObject){
        return isNotPrivate(learningObject) || canUpdate(user, learningObject);
    }

    /**
     * Learning object you can interacting with
     */
    default boolean canInteract(User user, ILearningObject learningObject){
        return isPublic(learningObject) || canUpdate(user, learningObject);
    }

    /**
     * Updating object
     */
    boolean canUpdate(User user, ILearningObject learningObject);

    /**
     * Public. Deleted items are private.
     */
    boolean isPublic(ILearningObject learningObject);

    /**
     * Public & not listed. Something not private. Deleted items are private.
     */
    boolean isNotPrivate(ILearningObject learningObject);
}