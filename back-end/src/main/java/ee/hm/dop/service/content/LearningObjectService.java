package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
<<<<<<< HEAD
<<<<<<< HEAD
import ee.hm.dop.service.learningObject.PermissionItem;
import ee.hm.dop.service.learningObject.PermissionFactory;
import ee.hm.dop.utils.ValidatorUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
=======
=======
import ee.hm.dop.model.interfaces.ILearningObject;
>>>>>>> new-develop
import ee.hm.dop.service.permission.PermissionItem;
import ee.hm.dop.service.permission.PermissionFactory;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.ValidatorUtil;

import javax.inject.Inject;
>>>>>>> new-develop

public class LearningObjectService {

    @Inject
    private LearningObjectDao learningObjectDao;
<<<<<<< HEAD

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = getLearningObjectDao().findById(learningObjectId);
        return canAcess(user, learningObject) ? learningObject : null;
    }

    public boolean canAcess(User user, LearningObject learningObject) {
=======
    @Inject
    private SolrEngineService solrEngineService;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = learningObjectDao.findById(learningObjectId);
        return canAccess(user, learningObject) ? learningObject : null;
    }

    public void incrementViewCount(LearningObject learningObject) {
        LearningObject originalPortfolio = validateAndFindIncludeDeleted(learningObject);
        learningObjectDao.incrementViewCount(originalPortfolio);
        solrEngineService.updateIndex();
    }

    public boolean canAccess(User user, LearningObject learningObject) {
>>>>>>> new-develop
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canInteract(user, learningObject);
    }

    public boolean canView(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canView(user, learningObject);
    }

<<<<<<< HEAD
=======
    public boolean canUpdate(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canUpdate(user, learningObject);
    }

>>>>>>> new-develop
    public LearningObject validateAndFind(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdNotDeleted);
    }

<<<<<<< HEAD
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
=======
    public LearningObject validateAndFindIncludeDeleted(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findById);
    }

    public LearningObject validateAndFindDeletedOnly(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdDeleted);
    }

    private PermissionItem getLearningObjectHandler(LearningObject learningObject) {
        return PermissionFactory.get(learningObject.getClass());
    }
>>>>>>> new-develop
}
