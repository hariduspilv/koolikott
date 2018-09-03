package ee.hm.dop.service.permission;

import ee.hm.dop.model.User;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.utils.UserUtil;

public interface PermissionItem {

    boolean canView(User user, ILearningObject learningObject);

    boolean canInteract(User user, ILearningObject learningObject);

    default boolean canUpdate(User user, ILearningObject learningObject) {
        if (learningObject == null) return false;
        return UserUtil.isAdminOrModerator(user) || UserUtil.isCreator(learningObject, user);
    }

    /**
     * Public. Deleted items are private.
     */
    default boolean isPublic(ILearningObject learningObject) {
        return learningObject != null && learningObject.getVisibility().isPublic() && !learningObject.isDeleted();
    }

    /**
     * Public & not listed. Something not private. Deleted items are private.
     */
    default boolean isNotPrivate(ILearningObject learningObject) {
        return learningObject != null && learningObject.getVisibility().isNotPrivate() && !learningObject.isDeleted();
    }
}