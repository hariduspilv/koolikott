package ee.hm.dop.service.content;

import ee.hm.dop.model.User;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.interfaces.IPortfolio;
import ee.hm.dop.service.learningObject.PermissionItem;
import ee.hm.dop.utils.UserUtil;

public class PortfolioPermission implements PermissionItem {

    @Override
    public boolean canView(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IPortfolio)) return false;
        return isNotPrivate(learningObject) || UserUtil.isAdminOrModerator(user) || UserUtil.isCreator(learningObject, user);
    }

    @Override
    public boolean canInteract(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IPortfolio)) return false;
        return isPublic(learningObject) || UserUtil.isAdminOrModerator(user) || UserUtil.isCreator(learningObject, user);
    }

    @Override
    public boolean canUpdate(User user, ILearningObject learningObject) {
        if (learningObject == null || !(learningObject instanceof IPortfolio)) return false;
        return UserUtil.isAdminOrModerator(user) || UserUtil.isCreator(learningObject, user);
    }
}
