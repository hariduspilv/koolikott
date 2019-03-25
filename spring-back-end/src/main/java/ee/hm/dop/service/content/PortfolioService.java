package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.service.permission.PortfolioPermission;
import ee.hm.dop.service.reviewmanagement.ChangeProcessStrategy;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.synchronizer.UpdatePortfolioMaterials;
import ee.hm.dop.utils.TextFieldUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static java.time.LocalDateTime.now;

@Service
@Transactional
public class PortfolioService {

    public static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    public static final String NUMBER_REGEX = "\\d+";
    public static final Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
    public static final Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

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
    @Inject
    private MaterialDao materialDao;
    @Inject
    private PortfolioMaterialDao portfolioMaterialDao;

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
        updatePortfolioMaterials(portfolio,chapterPattern,numberPattern);
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

        updatePortfolioMaterials(portfolio, chapterPattern, numberPattern);
        return createdPortfolio;
    }

    private void updatePortfolioMaterials(Portfolio portfolio, Pattern chapterPattern, Pattern numberPattern) {
        for (Chapter chapter : portfolio.getChapters()) {
            if (chapter.getBlocks() != null) {
                for (ChapterBlock block : chapter.getBlocks()) {
                    if (StringUtils.isNotBlank(block.getHtmlContent())) {
                        Matcher matcher = chapterPattern.matcher(block.getHtmlContent());
                        while (matcher.find()) {
                            Matcher numberMatcher = numberPattern.matcher(matcher.group());
                            while (numberMatcher.find()) {
                                if (!portfolioMaterialDao.materialToPortfolioConnected(materialDao.findById(Long.valueOf(numberMatcher.group())), portfolio)) {
                                    PortfolioMaterial portfolioMaterial = new PortfolioMaterial();
                                    portfolioMaterial.setPortfolio(portfolio);
                                    portfolioMaterial.setMaterial(materialDao.findById(Long.valueOf(numberMatcher.group())));
                                    portfolioMaterialDao.createOrUpdate(portfolioMaterial);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Portfolio validateUpdate(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());
        if (!portfolioPermission.canUpdate(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }
        validateTitle(portfolio);
        return originalPortfolio;
    }

    private Portfolio validateCopy(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioDao.findByIdNotDeleted(portfolio.getId());
        if (!portfolioPermission.canView(loggedInUser, originalPortfolio)) {
            throw ValidatorUtil.permissionError();
        }
        validateTitle(portfolio);
        return originalPortfolio;
    }

    private void validateTitle(Portfolio portfolio) {
        if (isEmpty(portfolio.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required field title must be filled.");
        }
    }
}
