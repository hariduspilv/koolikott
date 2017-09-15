package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.UserFavoriteDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

public class UserFavoriteService {

    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private UserFavoriteDao userFavoriteDao;

    public UserFavorite addUserFavorite(LearningObject learningObject, User loggedInUser) {
        validateLearningObjectAndIdNotNull(learningObject);

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setAdded(DateTime.now());
        userFavorite.setCreator(loggedInUser);
        userFavorite.setLearningObject(learningObject);

        return userFavoriteDao.createOrUpdate(userFavorite);
    }

    public void removeUserFavorite(Long id, User loggedInUser) {
        LearningObject learningObject = learningObjectDao.findById(id);

        validateLearningObjectAndIdNotNull(learningObject);
        userFavoriteDao.deleteByLearningObjectAndUser(learningObject, loggedInUser);
    }

    public UserFavorite hasFavorited(Long id, User loggedInUser) {
        if(id == null || loggedInUser == null) return null;
        return userFavoriteDao.findFavoriteByUserAndLearningObject(id, loggedInUser);
    }

    public List<ReducedLearningObject> getUserFavorites(User loggedInUser, int start, int maxResult) {
        return userFavoriteDao.findUsersFavoritedLearningObjects(loggedInUser, start, maxResult);
    }

    public long getUserFavoritesSize(User loggedInUser) {
        return userFavoriteDao.findUsersFavoritedLearningObjectsCount(loggedInUser);
    }

    private void validateLearningObjectAndIdNotNull(LearningObject learningObject) {
        if (learningObject == null || learningObject.getId() == null) {
            throw new RuntimeException("LearningObject not found");
        }
    }
}
