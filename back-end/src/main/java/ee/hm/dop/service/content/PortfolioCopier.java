package ee.hm.dop.service.content;

import com.google.common.collect.Lists;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
<<<<<<< HEAD
=======
import ee.hm.dop.service.permission.PortfolioPermission;
>>>>>>> new-develop
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
<<<<<<< HEAD
import java.util.ArrayList;
=======
>>>>>>> new-develop
import java.util.List;
import java.util.stream.Collectors;

public class PortfolioCopier {

    @Inject
    private PortfolioService portfolioService;
    @Inject
<<<<<<< HEAD
    private PortfolioConverter portfolioConverter;

    public Portfolio copy(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioService.findValid(portfolio);

        if (!portfolioService.canView(loggedInUser, originalPortfolio)) {
=======
    private PortfolioGetter portfolioGetter;
    @Inject
    private PortfolioConverter portfolioConverter;
    @Inject
    private PortfolioPermission portfolioPermission;

    public Portfolio copy(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioGetter.findValid(portfolio);

        if (!portfolioPermission.canView(loggedInUser, originalPortfolio)) {
>>>>>>> new-develop
            throw ValidatorUtil.permissionError();
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
