package ee.hm.dop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.inject.Inject;

import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.User;
import ee.hm.dop.model.Visibility;
import ee.hm.dop.service.learningObject.LearningObjectHandler;
import ee.hm.dop.service.learningObject.LearningObjectHandlerFactory;

public class LearningObjectService {

    @Inject
    private LearningObjectDAO learningObjectDAO;

    @Inject
    private SearchEngineService searchEngineService;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = learningObjectDAO.findById(learningObjectId);

        if (!hasAccess(user, learningObject)) {
            learningObject = null;
        }

        return learningObject;
    }

    public boolean hasAccess(User user, LearningObject learningObject) {
        if (learningObject == null) {
            return false;
        }

        LearningObjectHandler learningObjectHandler = LearningObjectHandlerFactory.get(learningObject.getClass());
        return learningObjectHandler.hasAccess(user, learningObject);
    }

    public LearningObject addTag(LearningObject learningObject, Tag tag, User user) {
        LearningObject updatedLearningObject;
        if (!hasAccess(user, learningObject)) {
            throw new RuntimeException("Access denied");
        }

        List<Tag> tags = learningObject.getTags();
        if (tags.contains(tag)) {
            throw new RuntimeException("Learning Object already contains tag");
        } else {
            tags.add(tag);
            updatedLearningObject = learningObjectDAO.update(learningObject);
            searchEngineService.updateIndex();
        }

        return updatedLearningObject;
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

            learningObjects.removeIf(this::isNotPublic);
            returnableLearningObjects.addAll(learningObjects);
            startPosition = count;
            count = numberOfLearningObjects - returnableLearningObjects.size();
        }

        return returnableLearningObjects;
    }

    public List<LearningObject> getNewestLearningObjects(int numberOfLearningObjects) {
        return getPublicLearningObjects(numberOfLearningObjects, learningObjectDAO::findNewestLearningObjects);
    }

    public List<LearningObject> getPopularLearningObjects(int numberOfLearningObjects) {
        return getPublicLearningObjects(numberOfLearningObjects, learningObjectDAO::findPopularLearningObjects);
    }

    private boolean isNotPublic(LearningObject learningObject) {
        return learningObject instanceof Portfolio && ((Portfolio) learningObject).getVisibility() != Visibility.PUBLIC;
    }
}
