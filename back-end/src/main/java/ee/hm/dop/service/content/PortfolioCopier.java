package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class PortfolioCopier {

    @Inject
    private PortfolioService portfolioService;
    @Inject
    private PortfolioConverter portfolioConverter;
    @Inject
    private PortfolioDao portfolioDao;

    public Portfolio copy(Portfolio portfolio, User loggedInUser) {
        if (portfolio.getId() == null) {
            throw new RuntimeException("Portfolio not found");
        }

        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        Portfolio copy = portfolioConverter.getPortfolioWithAllowedFieldsOnCreate(originalPortfolio);
        copy.setChapters(copyChapters(originalPortfolio.getChapters()));

        return portfolioService.doCreate(copy, loggedInUser, originalPortfolio.getCreator());
    }

    private List<Chapter> copyChapters(List<Chapter> chapters) {
        List<Chapter> copyChapters = new ArrayList<>();

        if (chapters != null) {
            for (Chapter chapter : chapters) {
                Chapter copy = new Chapter();
                copy.setTitle(chapter.getTitle());
                copy.setText(chapter.getText());
                copy.setContentRows(chapter.getContentRows());
                copy.setSubchapters(copyChapters(chapter.getSubchapters()));

                copyChapters.add(copy);
            }
        }

        return copyChapters;
    }
}
