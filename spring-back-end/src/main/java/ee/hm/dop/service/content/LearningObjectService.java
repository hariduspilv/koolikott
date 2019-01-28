package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.service.permission.PermissionFactory;
import ee.hm.dop.service.permission.PermissionItem;
import ee.hm.dop.utils.ValidatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class LearningObjectService {

    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private PermissionFactory permissionFactory;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = learningObjectDao.findById(learningObjectId);
        return canAccess(user, learningObject) ? learningObject : null;
    }

    public void incrementViewCount(LearningObject learningObject) {
        LearningObject originalLearningObject = validateAndFindIncludeDeleted(learningObject);
        learningObjectDao.incrementViewCount(originalLearningObject);
    }

    public boolean canAccess(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canInteract(user, learningObject);
    }

    public boolean canView(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canView(user, learningObject);
    }

    public boolean canUpdate(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canUpdate(user, learningObject);
    }

    public LearningObject validateAndFind(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdNotDeleted);
    }

    public LearningObject validateAndFindIncludeDeleted(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findById);
    }

    public LearningObject validateAndFindDeletedOnly(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdDeleted);
    }

    private PermissionItem getLearningObjectHandler(LearningObject learningObject) {
        return permissionFactory.get(learningObject);
    }
}