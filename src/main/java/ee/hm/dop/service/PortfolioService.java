package ee.hm.dop.service;

import static ee.hm.dop.model.Visibility.PRIVATE;
import static ee.hm.dop.utils.UserUtils.isAdmin;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;

import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.dao.UserLikeDAO;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.model.Visibility;
import ee.hm.dop.service.learningObject.LearningObjectHandler;

public class PortfolioService implements LearningObjectHandler {

    @Inject
    private PortfolioDAO portfolioDAO;

    @Inject
    private UserLikeDAO userLikeDAO;

    @Inject
    private SearchEngineService searchEngineService;

    public Portfolio get(long portfolioId, User loggedInUser) {
        Portfolio portfolio;
        if (isUserAdmin(loggedInUser)) {
            portfolio = portfolioDAO.findByIdFromAll(portfolioId);
        } else {
            portfolio = portfolioDAO.findByIdNotDeleted(portfolioId);

            if (portfolio != null && !isPortfolioAccessibleToUser(portfolio, loggedInUser)) {
                portfolio = null;
            }
        }

        return portfolio;
    }

    public List<Portfolio> getByCreator(User creator, User loggedInUser) {
        List<Portfolio> portfolios = new ArrayList<>();
        portfolioDAO.findByCreator(creator).stream().forEach(portfolio -> portfolios.add((Portfolio) portfolio));

        List<Portfolio> visiblePortfolios = portfolios.stream().filter(p -> isPortfolioVisibleToUser(p, loggedInUser))
                .collect(Collectors.toList());

        return visiblePortfolios;
    }

    public void incrementViewCount(Portfolio portfolio) {
        Portfolio originalPortfolio = portfolioDAO.findByIdFromAll(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        portfolioDAO.incrementViewCount(originalPortfolio);

        searchEngineService.updateIndex();
    }

    public void addComment(Comment comment, Portfolio portfolio, User loggedInUser) {
        if (isEmpty(comment.getText())) {
            throw new RuntimeException("Comment is missing text.");
        }

        if (comment.getId() != null) {
            throw new RuntimeException("Comment already exists.");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        if (!isPortfolioAccessibleToUser(originalPortfolio, loggedInUser)) {
            throw new RuntimeException("Portfolio not found");
        }

        comment.setAdded(DateTime.now());
        originalPortfolio.getComments().add(comment);
        portfolioDAO.update(originalPortfolio);
    }

    public UserLike addUserLike(Portfolio portfolio, User loggedInUser, boolean isLiked) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }
        if (!isPortfolioAccessibleToUser(originalPortfolio, loggedInUser)) {
            throw new RuntimeException("Not authorized");
        }

        userLikeDAO.deletePortfolioLike(originalPortfolio, loggedInUser);

        UserLike like = new UserLike();
        like.setLearningObject(originalPortfolio);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDAO.update(like);
    }

    public void removeUserLike(Portfolio portfolio, User loggedInUser) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }
        if (!isPortfolioAccessibleToUser(originalPortfolio, loggedInUser)) {
            throw new RuntimeException("Not authorized");
        }

        userLikeDAO.deletePortfolioLike(originalPortfolio, loggedInUser);
    }

    public UserLike getUserLike(Portfolio portfolio, User loggedInUser) {

        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
        Portfolio originalPortfolio = portfolioDAO.findByIdFromAll(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }
        if (!isPortfolioAccessibleToUser(originalPortfolio, loggedInUser)) {
            throw new RuntimeException("Not authorized");
        }

        UserLike like = userLikeDAO.findPortfolioUserLike(originalPortfolio, loggedInUser);
        return like;
    }

    public Recommendation addRecommendation(Portfolio portfolio, User loggedInUser) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null || !isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Portfolio not found");
        }

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(DateTime.now());

        originalPortfolio.setRecommendation(recommendation);

        originalPortfolio = (Portfolio) portfolioDAO.update(originalPortfolio);
        searchEngineService.updateIndex();

        return originalPortfolio.getRecommendation();
    }

    public void removeRecommendation(Portfolio portfolio, User loggedInUser) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null || !isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Portfolio not found");
        }

        originalPortfolio.setRecommendation(null);

        portfolioDAO.update(originalPortfolio);
        searchEngineService.updateIndex();
    }

    public Portfolio create(Portfolio portfolio, User creator) {
        if (portfolio.getId() != null) {
            throw new RuntimeException("Portfolio already exists.");
        }

        Portfolio safePortfolio = getPortfolioWithAllowedFieldsOnCreate(portfolio);
        return doCreate(safePortfolio, creator, creator);
    }

    private Portfolio doCreate(Portfolio portfolio, User creator, User originalCreator) {
        portfolio.setViews(0L);
        portfolio.setCreator(creator);
        portfolio.setOriginalCreator(originalCreator);
        portfolio.setVisibility(Visibility.PRIVATE);
        portfolio.setAdded(now());

        Portfolio createdPortfolio = (Portfolio) portfolioDAO.update(portfolio);
        searchEngineService.updateIndex();

        return createdPortfolio;
    }

    public Portfolio update(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = validateUpdate(portfolio, loggedInUser);

        originalPortfolio = setPortfolioUpdatableFields(originalPortfolio, portfolio);
        originalPortfolio.setUpdated(now());

        Portfolio updatedPortfolio = (Portfolio) portfolioDAO.update(originalPortfolio);
        searchEngineService.updateIndex();

        return updatedPortfolio;
    }

    private Portfolio validateUpdate(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio must already exist.");
        }

        if (isEmpty(portfolio.getTitle())) {
            throw new RuntimeException("Required field title must be filled.");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        if (originalPortfolio.getCreator().getId() != loggedInUser.getId()) {
            throw new RuntimeException("Logged in user must be the creator of this portfolio.");
        }

        return originalPortfolio;
    }

    public Portfolio copy(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        if (!isPortfolioAccessibleToUser(originalPortfolio, loggedInUser)) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio copy = getPortfolioWithAllowedFieldsOnCreate(originalPortfolio);
        copy.setChapters(copyChapters(originalPortfolio.getChapters()));

        return doCreate(copy, loggedInUser, originalPortfolio.getCreator());
    }

    public void delete(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio must already exist.");
        }

        Portfolio originalPortfolio = portfolioDAO.findByIdNotDeleted(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        if (originalPortfolio.getCreator().getId() != loggedInUser.getId() && !isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be the creator of this portfolio or administrator.");
        }

        portfolioDAO.delete(originalPortfolio);
        searchEngineService.updateIndex();
    }

    public void restore(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioDAO.findDeletedById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be an administrator.");
        }

        portfolioDAO.restore(originalPortfolio);
        searchEngineService.updateIndex();
    }

    public boolean isPortfolioAccessibleToUser(Portfolio portfolio, User loggedInUser) {
        return (portfolio.getVisibility() != Visibility.PRIVATE || isUserPortfolioCreator(portfolio, loggedInUser)
                && !portfolio.isDeleted())
                || isUserAdmin(loggedInUser);
    }

    private List<Chapter> copyChapters(List<Chapter> chapters) {
        List<Chapter> copyChapters = new ArrayList<>();

        if (chapters != null) {
            for (Chapter chapter : chapters) {
                Chapter copy = new Chapter();
                copy.setTitle(chapter.getTitle());
                copy.setText(chapter.getText());
                copy.setMaterials(chapter.getMaterials());
                copy.setSubchapters(copyChapters(chapter.getSubchapters()));

                copyChapters.add(copy);
            }
        }

        return copyChapters;
    }

    private Portfolio getPortfolioWithAllowedFieldsOnCreate(Portfolio portfolio) {
        Portfolio safePortfolio = new Portfolio();
        safePortfolio.setTitle(portfolio.getTitle());
        safePortfolio.setSummary(portfolio.getSummary());
        safePortfolio.setTags(portfolio.getTags());
        safePortfolio.setTargetGroups(portfolio.getTargetGroups());
        safePortfolio.setTaxon(portfolio.getTaxon());
        safePortfolio.setChapters(portfolio.getChapters());
        safePortfolio.setPicture(portfolio.getPicture());

        return safePortfolio;
    }

    private Portfolio setPortfolioUpdatableFields(Portfolio originalPortfolio, Portfolio portfolio) {
        originalPortfolio.setTitle(portfolio.getTitle());
        originalPortfolio.setSummary(portfolio.getSummary());
        originalPortfolio.setTags(portfolio.getTags());
        originalPortfolio.setTargetGroups(portfolio.getTargetGroups());
        originalPortfolio.setTaxon(portfolio.getTaxon());
        originalPortfolio.setChapters(portfolio.getChapters());
        originalPortfolio.setVisibility(portfolio.getVisibility());
        originalPortfolio.setPicture(portfolio.getPicture());
        return originalPortfolio;
    }

    private boolean isPortfolioVisibleToUser(Portfolio portfolio, User loggedInUser) {
        return portfolio.getVisibility() == Visibility.PUBLIC || isUserPortfolioCreator(portfolio, loggedInUser)
                || isUserAdmin(loggedInUser);
    }

    private boolean isUserPortfolioCreator(Portfolio portfolio, User loggedInUser) {
        return loggedInUser != null && portfolio.getCreator().getId() == loggedInUser.getId();
    }

    private boolean isUserAdmin(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.ADMIN;
    }

    public List<Portfolio> getDeletedPortfolios() {
        List<Portfolio> portfolios = new ArrayList<>();
        portfolioDAO.findDeletedPortfolios().stream().forEach(portfolio -> portfolios.add((Portfolio) portfolio));
        return portfolios;
    }

    @Override
    public boolean hasAccess(User user, LearningObject learningObject) {
        if (!(learningObject instanceof Portfolio)) {
            return false;
        }

        Portfolio portfolio = (Portfolio) learningObject;

        if (!isAdmin(user)) {
            if (portfolio.isDeleted()) {
                return false;
            }

            if (portfolio.getVisibility() == PRIVATE && portfolio.getCreator().getId() != user.getId()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isPublic(LearningObject learningObject) {
        return ((Portfolio) learningObject).getVisibility() == Visibility.PUBLIC;
    }
}
