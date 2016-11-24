package ee.hm.dop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.inject.Inject;

import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.dao.UserFavoriteDAO;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import ee.hm.dop.service.learningObject.LearningObjectHandler;
import ee.hm.dop.service.learningObject.LearningObjectHandlerFactory;
import org.joda.time.DateTime;

public class LearningObjectService extends BaseService {

    @Inject
    private LearningObjectDAO learningObjectDAO;

    @Inject
    private SolrEngineService solrEngineService;

    @Inject
    private UserFavoriteDAO userFavoriteDAO;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = getLearningObjectDAO().findById(learningObjectId);

        if (!hasPermissionsToAccess(user, learningObject)) {
            learningObject = null;
        }

        return learningObject;
    }

    protected boolean hasPermissionsToAccess(User user, LearningObject learningObject) {
        if (learningObject == null) {
            return false;
        }

        LearningObjectHandler learningObjectHandler = getLearningObjectHandler(learningObject);
        return learningObjectHandler.hasPermissionsToAccess(user, learningObject);
    }

    public LearningObject addTag(LearningObject learningObject, Tag tag, User user) {
        LearningObject updatedLearningObject;
        if (!hasPermissionsToAccess(user, learningObject)) {
            throw new RuntimeException("Access denied");
        }

        List<Tag> tags = learningObject.getTags();
        if (tags.contains(tag)) {
            throw new RuntimeException("Learning Object already contains tag");
        }

        tags.add(tag);
        updatedLearningObject = getLearningObjectDAO().update(learningObject);
        solrEngineService.updateIndex();

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

            learningObjects.removeIf(learningObject -> !getLearningObjectHandler(learningObject).isPublic(
                    learningObject));
            returnableLearningObjects.addAll(learningObjects);
            startPosition += count;
            count = numberOfLearningObjects - returnableLearningObjects.size();
        }

        return returnableLearningObjects;
    }

    public List<LearningObject> getNewestLearningObjects(int numberOfLearningObjects) {
        return getPublicLearningObjects(numberOfLearningObjects, getLearningObjectDAO()::findNewestLearningObjects);
    }

    protected LearningObjectHandler getLearningObjectHandler(LearningObject learningObject) {
        return LearningObjectHandlerFactory.get(learningObject.getClass());
    }

    protected LearningObjectDAO getLearningObjectDAO() {
        return learningObjectDAO;
    }

    public UserFavorite addUserFavorite(LearningObject learningObject, User loggedInUser) {
        validateLearningObjectAndIdNotNull(learningObject);

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setAdded(DateTime.now());
        userFavorite.setCreator(loggedInUser);
        userFavorite.setLearningObject(learningObject);

        return userFavoriteDAO.update(userFavorite);
    }

    public void removeUserFavorite(Long id, User loggedInUser) {
        LearningObject learningObject = learningObjectDAO.findById(id);

        validateLearningObjectAndIdNotNull(learningObject);
        userFavoriteDAO.deleteByLearningObjectAndUser(learningObject, loggedInUser);
    }

    public UserFavorite hasFavorited(Long id, User loggedInUser) {
        return userFavoriteDAO.findFavoriteByUserAndLearningObject(id, loggedInUser);
    }

    public List<LearningObject> getUserFavorites(User loggedInUser, int start, int maxResult) {
        return userFavoriteDAO.findUsersFavoritedLearningObjects(loggedInUser, start, maxResult);
    }

    private void validateLearningObjectAndIdNotNull(LearningObject learningObject) {
        if (learningObject == null || learningObject.getId() == null) {
            throw new RuntimeException("LearningObject not found");
        }
    }

    public long getUserFavoritesSize(User loggedInUser) {
        return userFavoriteDAO.findUsersFavoritedLearningObjectsCount(loggedInUser);
    }
}
