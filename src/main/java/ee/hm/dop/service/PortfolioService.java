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

        comment.setCreated(DateTime.now());
        originalPortfolio.getComments().add(comment);
        portfolioDAO.update(originalPortfolio);
    }
}
