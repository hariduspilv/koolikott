package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.service.learningObject.PermissionItem;
import ee.hm.dop.service.learningObject.PermissionFactory;
import ee.hm.dop.utils.ValidatorUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LearningObjectService {

    @Inject
    private LearningObjectDao learningObjectDao;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = getLearningObjectDao().findById(learningObjectId);
        return canAcess(user, learningObject) ? learningObject : null;
    }

    public boolean canAcess(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canInteract(user, learningObject);
    }

    public boolean canView(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canView(user, learningObject);
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

    private List<LearningObject> getPublicLearningObjects(int numberOfLearningObjects,
                                                          BiFunction<Integer, Integer, List<LearningObject>> functionToGetLearningObjects) {
        List<LearningObject> returnableLearningObjects = new ArrayList<>();
        int startPosition = 0;
        int count = numberOfLearningObjects;
        while (returnableLearningObjects.size() != numberOfLearningObjects) {
            List<LearningObject> learningObjects = functionToGetLearningObjects.apply(count, startPosition);
            if (learningObjects.size() == 0) {
                break;
            }

            learningObjects.removeIf(learningObject -> !getLearningObjectHandler(learningObject).isPublic(learningObject));
            returnableLearningObjects.addAll(learningObjects);
            startPosition += count;
            count = numberOfLearningObjects - returnableLearningObjects.size();
        }

        return returnableLearningObjects;
    }

    List<LearningObject> getNewestLearningObjects(int numberOfLearningObjects) {
        return getPublicLearningObjects(numberOfLearningObjects, getLearningObjectDao()::findNewestLearningObjects);
    }

    PermissionItem getLearningObjectHandler(LearningObject learningObject) {
        return PermissionFactory.get(learningObject.getClass());
    }

    LearningObjectDao getLearningObjectDao() {
        return learningObjectDao;
    }
}
