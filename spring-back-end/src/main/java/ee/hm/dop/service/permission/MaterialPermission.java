package ee.hm.dop.service.permission;

import ee.hm.dop.model.User;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.interfaces.IMaterial;
import ee.hm.dop.utils.UserUtil;
import org.springframework.stereotype.Service;

@Service
public class MaterialPermission implements PermissionItem {

    @Override
    public boolean canView(User user, ILearningObject learningObject) {
        if (!(learningObject instanceof IMaterial)) return false;
        return isNotPrivate(learningObject) || UserUtil.isAdmin(user);
    }

    @Override
    public boolean canInteract(User user, ILearningObject learningObject) {
        if (!(learningObject instanceof IMaterial)) return false;
        return isPublic(learningObject) || UserUtil.isAdmin(user);
    }
}
