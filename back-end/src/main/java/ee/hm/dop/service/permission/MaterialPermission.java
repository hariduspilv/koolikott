package ee.hm.dop.service.permission;

import ee.hm.dop.model.User;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.interfaces.IMaterial;
import ee.hm.dop.utils.UserUtil;

public class MaterialPermission implements PermissionItem {

    @Override
    public boolean canView(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IMaterial)) return false;
        return isNotPrivate(learningObject) || UserUtil.isAdmin(user);
    }

    @Override
    public boolean canInteract(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IMaterial)) return false;
        return isPublic(learningObject) || UserUtil.isAdmin(user);
    }

    @Override
    public boolean canUpdate(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IMaterial)) return false;
        return UserUtil.isAdminOrModerator(user) || UserUtil.isCreator(learningObject, user);
    }
}
