package ee.hm.dop.service.content;

import com.google.common.collect.Lists;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PortfolioCopier {

    @Inject
    private PortfolioService portfolioService;
    @Inject
    private PortfolioConverter portfolioConverter;

    public Portfolio copy(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioService.findValid(portfolio);

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
            throw new RuntimeException("Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.");
        }

        Portfolio copy = portfolioConverter.getPortfolioWithAllowedFieldsOnCreate(originalPortfolio);
        copy.setChapters(copyChapters(originalPortfolio.getChapters()));

        return portfolioService.doCreate(copy, loggedInUser, originalPortfolio.getCreator());
    }

    private List<Chapter> copyChapters(List<Chapter> chapters) {
        if (CollectionUtils.isEmpty(chapters)) {
            return Lists.newArrayList();
        }
        return chapters.stream().map(this::copy).collect(Collectors.toList());
    }

    private Chapter copy(Chapter chapter) {
        Chapter copy = new Chapter();
        copy.setTitle(chapter.getTitle());
        copy.setText(chapter.getText());
        copy.setContentRows(chapter.getContentRows());
        copy.setSubchapters(copyChapters(chapter.getSubchapters()));
        return copy;
    }
}
