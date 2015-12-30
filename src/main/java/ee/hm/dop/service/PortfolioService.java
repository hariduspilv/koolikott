package ee.hm.dop.service;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;

import ee.hm.dop.dao.ImproperContentDAO;
import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.dao.UserLikeDAO;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.model.Visibility;

public class PortfolioService {

    @Inject
    private PortfolioDAO portfolioDAO;

    @Inject
    private UserLikeDAO userLikeDAO;

    @Inject
    private ImproperContentDAO improperContentDAO;

    @Inject
    private SearchEngineService searchEngineService;

    public Portfolio get(long portfolioId, User loggedInUser) {
        Portfolio portfolio = portfolioDAO.findById(portfolioId);

        if (portfolio != null && !isPortfolioAccessibleToUser(portfolio, loggedInUser)) {
            portfolio = null;
        }

        return portfolio;
    }

    public List<Portfolio> getByCreator(User creator, User loggedInUser) {
        List<Portfolio> portfolios = portfolioDAO.findByCreator(creator);

        portfolios = portfolios.stream().filter(p -> isPortfolioVisibleToUser(p, loggedInUser))
                .collect(Collectors.toList());

        return portfolios;
    }

    public byte[] getPortfolioPicture(Portfolio portfolio, User loggedInUser) {
        Portfolio actualPortfolio = portfolioDAO.findById(portfolio.getId());

        if (actualPortfolio != null && !isPortfolioAccessibleToUser(actualPortfolio, loggedInUser)) {
            return null;
        }

        return portfolioDAO.findPictureByPortfolio(portfolio);
    }

    public void incrementViewCount(Portfolio portfolio) {
        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        portfolioDAO.incrementViewCount(originalPortfolio);
    }

    public void addComment(Comment comment, Portfolio portfolio, User loggedInUser) {
        if (isEmpty(comment.getText())) {
            throw new RuntimeException("Comment is missing text.");
        }

        if (comment.getId() != null) {
            throw new RuntimeException("Comment already exists.");
        }

        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
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
        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }
        if (!isPortfolioAccessibleToUser(originalPortfolio, loggedInUser)) {
            throw new RuntimeException("Not authorized");
        }

        userLikeDAO.deletePortfolioLike(originalPortfolio, loggedInUser);

        UserLike like = new UserLike();
        like.setPortfolio(originalPortfolio);
        like.setCreator(loggedInUser);
        like.setLiked(isLiked);
        like.setAdded(DateTime.now());

        return userLikeDAO.update(like);
    }

    public void removeUserLike(Portfolio portfolio, User loggedInUser) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
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
        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }
        if (!isPortfolioAccessibleToUser(originalPortfolio, loggedInUser)) {
            throw new RuntimeException("Not authorized");
        }

        UserLike like = userLikeDAO.findPortfolioUserLike(originalPortfolio, loggedInUser);
        return like;
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

        Portfolio createdPortfolio = portfolioDAO.update(portfolio);
        searchEngineService.updateIndex();

        return createdPortfolio;
    }

    public Portfolio update(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio must already exist.");
        }

        if (isEmpty(portfolio.getTitle())) {
            throw new RuntimeException("Required field title must be filled.");
        }

        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        if (originalPortfolio.getCreator().getId() != loggedInUser.getId()) {
            throw new RuntimeException("Logged in user must be the creator of this portfolio.");
        }

        originalPortfolio = setPortfolioUpdatableFields(originalPortfolio, portfolio);

        Portfolio updatedPortfolio = portfolioDAO.update(originalPortfolio);
        searchEngineService.updateIndex();

        return updatedPortfolio;
    }

    public Portfolio copy(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
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

        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        if (originalPortfolio.getCreator().getId() != loggedInUser.getId() && !isUserAdmin(loggedInUser)) {
            throw new RuntimeException("Logged in user must be the creator of this portfolio or administrator.");
        }

        portfolioDAO.delete(originalPortfolio);
        searchEngineService.updateIndex();
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
        originalPortfolio.setPicture(portfolio.getPicture());
        originalPortfolio.setVisibility(portfolio.getVisibility());
        return originalPortfolio;
    }

    private boolean isPortfolioAccessibleToUser(Portfolio portfolio, User loggedInUser) {
        return portfolio.getVisibility() != Visibility.PRIVATE || isUserPortfolioCreator(portfolio, loggedInUser) || isUserAdmin(loggedInUser);
    }

    private boolean isPortfolioVisibleToUser(Portfolio portfolio, User loggedInUser) {
        return portfolio.getVisibility() == Visibility.PUBLIC || isUserPortfolioCreator(portfolio, loggedInUser) || isUserAdmin(loggedInUser);
    }

    private boolean isUserPortfolioCreator(Portfolio portfolio, User loggedInUser) {
        return loggedInUser != null && portfolio.getCreator().getId() == loggedInUser.getId();
    }

    private boolean isUserAdmin(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.ADMIN;
    }

    public ImproperContent addImproperPortfolio(Portfolio portfolio, User loggedInUser) {
        if (portfolio == null || portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }
        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }

        ImproperContent improperContent = new ImproperContent();
        improperContent.setCreator(loggedInUser);
        improperContent.setPortfolio(portfolio);
        improperContent.setAdded(DateTime.now());

        return improperContentDAO.update(improperContent);
    }

    public List<ImproperContent> getImproperPortfolios() {
        return improperContentDAO.getImproperPortfolios();
    }

    public Boolean hasSetImproper(long portfolioId, User loggedInUser) {
        List<ImproperContent> improperContents = improperContentDAO.findByPortfolioAndUser(portfolioId, loggedInUser);

        return improperContents.size() != 0;
    }
}
