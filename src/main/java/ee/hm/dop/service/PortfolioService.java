package ee.hm.dop.service;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;

import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

public class PortfolioService {

    @Inject
    private PortfolioDAO portfolioDAO;

    @Inject
    private SearchEngineService searchEngineService;

    public Portfolio get(long materialId) {
        return portfolioDAO.findById(materialId);
    }

    public List<Portfolio> getByCreator(User creator) {
        return portfolioDAO.findByCreator(creator);
    }

    public byte[] getPortfolioPicture(Portfolio portfolio) {
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
        safePortfolio.setViews(0L);
        safePortfolio.setCreator(creator);

        Portfolio createdPortfolio = portfolioDAO.update(safePortfolio);
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
        return originalPortfolio;
    }

}
