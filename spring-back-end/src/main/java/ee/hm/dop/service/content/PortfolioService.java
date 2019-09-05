package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioLogDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.service.files.PictureService;
import ee.hm.dop.service.permission.PortfolioPermission;
import ee.hm.dop.service.reviewmanagement.ChangeProcessStrategy;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.TextFieldUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.util.List;

import static ee.hm.dop.model.enums.SaveType.MANUAL;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@Transactional
public class PortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);
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
    private PortfolioMaterialService portfolioMaterialService;
    @Inject
    private PortfolioLogDao portfolioLogDao;
    @Inject
    private MediaService mediaService;
    @Inject
    private PictureService pictureService;

    public Portfolio create(Portfolio portfolio, User creator) {
        TextFieldUtil.cleanTextFields(portfolio);
        ValidatorUtil.mustNotHaveId(portfolio);
        validateTitle(portfolio);

        Portfolio portfolioCreated = save(portfolioConverter.setFieldsToNewPortfolio(portfolio), creator, creator);
        portfolioCreated.setSaveType(MANUAL);
        savePortfolioLog(portfolioConverter.setFieldsToNewPortfolioLog(portfolioCreated));
        return portfolioCreated;
    }

    public Portfolio update(Portfolio portfolio, User user) {
        TextFieldUtil.cleanTextFields(portfolio);

        Portfolio originalPortfolio = portfolioConverter.setFieldsToExistingPortfolio(validateUpdate(portfolio, user), portfolio);
        originalPortfolio.setUpdated(now());

        logger.info("Portfolio materials updating started. Portfolio id= " + portfolio.getId());
        Portfolio updatedPortfolio = portfolioDao.createOrUpdate(originalPortfolio);

        updatedPortfolio.setSaveType(portfolio.getSaveType());

        PortfolioLog portfolioLogUpdated = savePortfolioLog(portfolioConverter.setFieldsToNewPortfolioLog(updatedPortfolio));
        logger.info("Portfolio with id: " + portfolio.getId() + " ,history log with id:" + portfolioLogUpdated.getId() + " added");

        portfolioMaterialService.update(portfolio);
        logger.info("Portfolio materials updating ended");

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

    public Portfolio findById(Long id) {
        return portfolioDao.findById(id);
    }

    public boolean portfolioHasAcceptableLicenses(Long id) {
        Portfolio portfolio = findById(id);
        if (portfolio == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<Media> portfolioMedia = getPortfolioMedia(portfolio);
        return !portfolioHasUnAcceptableLicense(portfolio) &&
                portfolio.getPicture() != null && !portfolioThumbnailHasUnAcceptableLicense(portfolio.getPicture()) &&
                !portfolioMediaHasUnAcceptableLicenses(portfolioMedia);
    }

    private boolean portfolioMediaHasUnAcceptableLicenses(List<Media> portfolioMedia) {
        return mediaService.anyMediaHasUnacceptableLicenseType(portfolioMedia);
    }

    private List<Media> getPortfolioMedia(Portfolio portfolio) {
        return mediaService.getAllMediaIfLearningObjectIsPortfolio(portfolio);
    }

    private boolean portfolioThumbnailHasUnAcceptableLicense(Picture picture) {
        return pictureService.pictureHasUnAcceptableLicence(picture);
    }

    private boolean portfolioHasUnAcceptableLicense(Portfolio portfolio) {
        LicenseType licenseType = portfolio.getLicenseType();
        if (licenseType == null) {
            return true;
        }
        return !licenseType.getName().equals("CCBY") &&
                !licenseType.getName().equals("CCBYSA") &&
                !licenseType.getName().equals("CCBYSA30");
    }

    private PortfolioLog savePortfolioLog(PortfolioLog portfolio) {
        return portfolioLogDao.createOrUpdate(portfolio);
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

        portfolioMaterialService.save(portfolio);
        return createdPortfolio;
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
