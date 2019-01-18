package ee.hm.dop.service.permission;

import ee.hm.dop.model.User;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.interfaces.IPortfolio;
import ee.hm.dop.utils.UserUtil;
import org.springframework.stereotype.Service;

@Service
public class PortfolioPermission implements PermissionItem {

    @Override
    public boolean canView(User user, ILearningObject learningObject) {
        if (!(learningObject instanceof IPortfolio)) return false;
        return isNotPrivate(learningObject) || UserUtil.isAdminOrModerator(user) || UserUtil.isCreator(learningObject, user);
    }

    @Override
    public boolean canInteract(User user, ILearningObject learningObject) {
        if (!(learningObject instanceof IPortfolio)) return false;
        return isPublic(learningObject) || UserUtil.isAdminOrModerator(user) || UserUtil.isCreator(learningObject, user);
    }
}
