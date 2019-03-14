package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterBlock;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.service.permission.PortfolioPermission;
import ee.hm.dop.service.reviewmanagement.ChangeProcessStrategy;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.TextFieldUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ee.hm.dop.utils.ValidatorUtil.permissionError;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

public class PortfolioService {

    public static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private ReviewableChangeService reviewableChangeService;
    @Inject
    private PortfolioConverter portfolioConverter;
    @Inject
    private FirstReviewAdminService firstReviewAdminService;
    @Inject
    private PortfolioPermission portfolioPermission;
    @Inject
    private PortfolioCopier portfolioCopier;

    public Portfolio create(Portfolio portfolio, User creator) {
        TextFieldUtil.cleanTextFields(portfolio);
        ValidatorUtil.mustNotHaveId(portfolio);
        validateTitle(portfolio);
        return save(portfolioConverter.setFieldsToNewPortfolio(portfolio), creator, creator);
    }

    public Portfolio update(Portfolio portfolio, User user) {
        TextFieldUtil.cleanTextFields(portfolio);

        Portfolio originalPortfolio = portfolioConverter.setFieldsToExistingPortfolio(validateUpdate(portfolio, user), portfolio);
        originalPortfolio.setUpdated(now());

        Portfolio updatedPortfolio = portfolioDao.createOrUpdate(originalPortfolio);

        boolean loChanged = reviewableChangeService.processChanges(updatedPortfolio, user, ChangeProcessStrategy.processStrategy(updatedPortfolio));
        if (loChanged) return portfolioDao.createOrUpdate(updatedPortfolio);

        solrEngineService.updateIndex();

        return updatedPortfolio;
    }

    public Portfolio copy(Portfolio portfolio, User loggedInUser) {
        TextFieldUtil.cleanTextFields(portfolio);

        Portfolio originalPortfolio = validateCopy(portfolio, loggedInUser);

        Portfolio copy = portfolioConverter.setFieldsToNewPortfolio(portfolio);
        copy.setChapters(portfolioCopier.copyChapters(originalPortfolio.getChapters()));

        return save(copy, loggedInUser, originalPortfolio.getCreator());
    }

    private Portfolio save(Portfolio portfolio, User creator, User originalCreator) {
        portfolio.setViews(0L);
        portfolio.setCreator(creator);
        portfolio.setOriginalCreator(originalCreator);
        portfolio.setVisibility(Visibility.PRIVATE);
        portfolio.setAdded(now());

        Portfolio createdPortfolio = portfolioDao.createOrUpdate(portfolio);
        firstReviewAdminService.save(createdPortfolio);
        solrEngineService.updateIndex();

        //do magic with materials
        List<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile(MATERIAL_REGEX);
        for (Chapter chapter : portfolio.getChapters()) {
            for (ChapterBlock block : chapter.getBlocks()) {
                if (StringUtils.isNotBlank(block.getHtmlContent())) {
                    Matcher matcher = pattern.matcher(block.getHtmlContent());
                    while (matcher.find()) {
                        results.add(matcher.group());
                    }
                }
            }
        }
        //domagic
        //transform strings to material ids
        List<Long> fromFrontIds = new ArrayList<>();
        //from db get current materials
        List<Long> dbIds = new ArrayList<>();

        List<Long> newToSave = new ArrayList<>();
        for (Long fromFrontId : fromFrontIds) {
            if (!dbIds.contains(fromFrontId)){
                newToSave.add(fromFrontId);
            }
        }
        List<Long> oldToRemove = new ArrayList<>();
        for (Long dbId : dbIds) {
            if (!fromFrontIds.contains(dbId)){
                oldToRemove.add(dbId);
            }
        }
        //dao.save
        //dao.remove


        return createdPortfolio;
    }

    private Portfolio validateUpdate(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());
        if (!portfolioPermission.canUpdate(loggedInUser, originalPortfolio)) {
            throw permissionError();
        }
        validateTitle(portfolio);
        return originalPortfolio;
    }

    private Portfolio validateCopy(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());
        if (!portfolioPermission.canView(loggedInUser, originalPortfolio)) {
            throw permissionError();
        }
        validateTitle(portfolio);
        return originalPortfolio;
    }

    private void validateTitle(Portfolio portfolio) {
        if (isEmpty(portfolio.getTitle())) {
            throw new WebApplicationException("Required field title must be filled.", Response.Status.BAD_REQUEST);
        }
    }
}
