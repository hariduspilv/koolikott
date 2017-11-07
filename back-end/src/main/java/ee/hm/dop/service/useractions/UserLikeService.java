package ee.hm.dop.service.useractions;

import static org.joda.time.DateTime.now;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.UserLikeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.Like;
<<<<<<< HEAD
=======
import ee.hm.dop.service.content.LearningObjectService;
>>>>>>> new-develop
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.utils.ValidatorUtil;
import org.joda.time.DateTime;

public class UserLikeService {

    @Inject
    private UserLikeDao userLikeDao;
    @Inject
<<<<<<< HEAD
    private MaterialService materialService;
    @Inject
    private PortfolioService portfolioService;

    public void removeUserLike(Material material, User loggedInUser) {
        Material originalMaterial = materialService.validateAndFindNotDeleted(material);
        userLikeDao.deleteMaterialLike(originalMaterial, loggedInUser);
    }

    public UserLike getUserLike(Material material, User loggedInUser) {
        ValidatorUtil.mustHaveId(material);
        return userLikeDao.findMaterialUserLike(material, loggedInUser);
    }


    public List<Searchable> getMostLiked(int maxResults) {
        // TODO: return only objects that user is allowed to see ex if private portfolio then, don't return
        return userLikeDao.findMostLikedSince(now().minusYears(1), maxResults);
    }

    public UserLike addUserLike(Material material, User loggedInUser, Like like) {
        Material originalMaterial = materialService.validateAndFindNotDeleted(material);
        userLikeDao.deleteMaterialLike(originalMaterial, loggedInUser);

        return save(originalMaterial, loggedInUser, like);
    }

    public UserLike addUserLike(Portfolio portfolio, User loggedInUser, Like like) {
        Portfolio originalPortfolio = portfolioService.findValid(portfolio);

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }

        userLikeDao.deletePortfolioLike(originalPortfolio, loggedInUser);

        return save(originalPortfolio, loggedInUser, like);
=======
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
>>>>>>> new-develop
    }

    private UserLike save(LearningObject learningObject, User loggedInUser, Like like) {
        UserLike userLike = new UserLike();
        userLike.setLearningObject(learningObject);
        userLike.setCreator(loggedInUser);
        userLike.setLiked(like.isLiked());
        userLike.setAdded(DateTime.now());
        return userLikeDao.update(userLike);
    }

<<<<<<< HEAD
    public void removeUserLike(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioService.findValid(portfolio);

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }

        userLikeDao.deletePortfolioLike(originalPortfolio, loggedInUser);
    }

    public UserLike getUserLike(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioService.findValid(portfolio);

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }

        return userLikeDao.findPortfolioUserLike(originalPortfolio, loggedInUser);
=======
    public void removeUserLike(LearningObject learningObject, User loggedInUser) {
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);

        if (!learningObjectService.canView(loggedInUser, originalLearningObject)) {
            throw ValidatorUtil.permissionError();
        }

        userLikeDao.deleteByLearningObjectAndUser(originalLearningObject, loggedInUser);
>>>>>>> new-develop
    }
}
