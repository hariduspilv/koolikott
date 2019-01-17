package ee.hm.dop.service.useractions;

import static org.joda.time.DateTime.now;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.UserLikeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.Like;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.utils.ValidatorUtil;
import org.joda.time.DateTime;

public class UserLikeService {

    @Inject
    private UserLikeDao userLikeDao;
    @Inject
    private LearningObjectService learningObjectService;

    public List<Searchable> getMostLiked(int maxResults) {
        return userLikeDao.findMostLikedSince(now().minusYears(1), maxResults);
    }

    public UserLike getUserLike(LearningObject portfolio, User loggedInUser) {
        LearningObject originalPortfolio = learningObjectService.validateAndFind(portfolio);

        if (!learningObjectService.canView(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }
        return userLikeDao.findByLearningObjectAndUser(originalPortfolio, loggedInUser);
    }

    public UserLike addUserLike(LearningObject learningObject, User loggedInUser, Like like) {
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);
        if (!learningObjectService.canView(loggedInUser, originalLearningObject)) {
            throw ValidatorUtil.permissionError();
        }
        userLikeDao.deleteByLearningObjectAndUser(originalLearningObject, loggedInUser);

        return save(originalLearningObject, loggedInUser, like);
    }

    private UserLike save(LearningObject learningObject, User loggedInUser, Like like) {
        UserLike userLike = new UserLike();
        userLike.setLearningObject(learningObject);
        userLike.setCreator(loggedInUser);
        userLike.setLiked(like.isLiked());
        userLike.setAdded(DateTime.now());
        return userLikeDao.update(userLike);
    }

    public void removeUserLike(LearningObject learningObject, User loggedInUser) {
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);

        if (!learningObjectService.canView(loggedInUser, originalLearningObject)) {
            throw ValidatorUtil.permissionError();
        }

        userLikeDao.deleteByLearningObjectAndUser(originalLearningObject, loggedInUser);
    }
}
