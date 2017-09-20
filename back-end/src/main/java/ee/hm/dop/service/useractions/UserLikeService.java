package ee.hm.dop.service.useractions;

import static org.joda.time.DateTime.now;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.UserLikeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.utils.ValidatorUtil;
import org.joda.time.DateTime;

public class UserLikeService {

    @Inject
    private UserLikeDao userLikeDao;
    @Inject
    private MaterialService materialService;
    @Inject
    private PortfolioService portfolioService;

    public List<Searchable> getMostLiked(int maxResults) {
        // TODO: return only objects that user is allowed to see ex if private portfolio then, don't return
        return userLikeDao.findMostLikedSince(now().minusYears(1), maxResults);
    }

    public UserLike addUserLike(Material material, User loggedInUser, boolean isLiked) {
        Material originalMaterial = materialService.validateAndFindNotDeleted(material);
        userLikeDao.deleteMaterialLike(originalMaterial, loggedInUser);

        return save(originalMaterial, loggedInUser, isLiked);
    }

    public UserLike addUserLike(Portfolio portfolio, User loggedInUser, boolean isLiked) {
        Portfolio originalPortfolio = portfolioService.findValid(portfolio);

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }

        userLikeDao.deletePortfolioLike(originalPortfolio, loggedInUser);

        return save(originalPortfolio, loggedInUser, isLiked);
    }

    private UserLike save(LearningObject learningObject, User loggedInUser, boolean isLiked) {
        UserLike like = new UserLike();
        like.setLearningObject(learningObject);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());
        return userLikeDao.update(like);
    }

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
    }
}
