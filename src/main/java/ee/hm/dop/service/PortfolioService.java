package ee.hm.dop.service;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;

import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.model.Visibility;

public class PortfolioService {

    @Inject
    private PortfolioDAO portfolioDAO;

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

        portfolios = portfolios.stream().filter(p -> isPortfolioAccessibleToUser(p, loggedInUser))
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

    public void addComment(Comment comment, Portfolio portfolio) {
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

        comment.setAdded(DateTime.now());
        originalPortfolio.getComments().add(comment);
        portfolioDAO.update(originalPortfolio);
    }

    public Portfolio create(Portfolio portfolio, User creator) {
        if (portfolio.getId() != null) {
            throw new RuntimeException("Portfolio already exists.");
        }

        Portfolio safePortfolio = getPortfolioWithAllowedFieldsOnCreate(portfolio);
        return doCreate(safePortfolio, creator);
    }

    private Portfolio doCreate(Portfolio portfolio, User creator) {
        portfolio.setViews(0L);
        portfolio.setCreator(creator);
        portfolio.setVisibility(Visibility.PUBLIC);

        Portfolio createdPortfolio = portfolioDAO.update(portfolio);
        searchEngineService.updateIndex();

        return createdPortfolio;
    }

    public Portfolio update(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio must already exist.");
        }

        if (portfolio.getCreator().getId() != loggedInUser.getId()) {
            throw new RuntimeException("Logged in user must be the creator of this portfolio.");
        }

        if (isEmpty(portfolio.getTitle())) {
            throw new RuntimeException("Required field title must be filled.");
        }

        Portfolio originalPortfolio = portfolioDAO.findById(portfolio.getId());
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
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

        Portfolio copy = getPortfolioWithAllowedFieldsOnCreate(originalPortfolio);
        copy.setChapters(copyChapters(originalPortfolio.getChapters()));

        return doCreate(copy, loggedInUser);
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
        if (portfolio.getVisibility() != Visibility.PRIVATE) {
            return true;
        } else {
            return loggedInUser != null && portfolio.getCreator().getId() == loggedInUser.getId();
        }
    }

}
