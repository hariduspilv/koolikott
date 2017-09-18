package ee.hm.dop.service.useractions;

import static org.joda.time.DateTime.now;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.UserLikeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.PortfolioService;
import org.joda.time.DateTime;

public class UserLikeService {

    @Inject
    private UserLikeDao userLikeDao;
    @Inject
    private MaterialService materialService;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private PortfolioService portfolioService;

    public List<Searchable> getMostLiked(int maxResults) {
        // TODO: return only objects that user is allowed to see ex if private portfolio then, don't return
        return userLikeDao.findMostLikedSince(now().minusYears(1), maxResults);
    }

    public UserLike addUserLike(Material material, User loggedInUser, boolean isLiked) {
        Material originalMaterial = materialService.validateAndFind(material);

        userLikeDao.deleteMaterialLike(originalMaterial, loggedInUser);

        UserLike like = new UserLike();
        like.setLearningObject(originalMaterial);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDao.update(like);
    }

    public UserLike addUserLike(Portfolio portfolio, User loggedInUser, boolean isLiked) {
        validate(portfolio);
        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        userLikeDao.deletePortfolioLike(originalPortfolio, loggedInUser);

        UserLike like = new UserLike();
        like.setLearningObject(originalPortfolio);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDao.update(like);
    }

    public void removeUserLike(Portfolio portfolio, User loggedInUser) {
        validate(portfolio);
        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        userLikeDao.deletePortfolioLike(originalPortfolio, loggedInUser);
    }

    public UserLike getUserLike(Portfolio portfolio, User loggedInUser) {
        validate(portfolio);
        Portfolio originalPortfolio = portfolioDao.findById(portfolio.getId());

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        return userLikeDao.findPortfolioUserLike(originalPortfolio, loggedInUser);
    }

    private void validate(Portfolio portfolio) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
    }
}
