package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.permission.PortfolioPermission;
import ee.hm.dop.service.reviewmanagement.ChangeProcessStrategy;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.TextFieldUtil;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.ValidatorUtil.permissionError;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joda.time.DateTime.now;

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

        logger.info("Portfolio materials updating started. Portfolio id= " + portfolio.getId());
        Portfolio updatedPortfolio = portfolioDao.createOrUpdate(originalPortfolio);

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

//    public boolean showUnreviewedPortfolio(Long id, User user) {
//        boolean answer = false;
//        if (UserUtil.isAdmin(user)) {
//            answer = true;
//        } else if (UserUtil.isModerator(user)) {
//            List<Long> collect = portfolioDao.findById(id).getTaxons().stream().mapToLong(Taxon::getId).boxed().collect(Collectors.toList());
//            List<Long> userTaxons = taxonDao.getUserTaxonsWithChildren(user);
//
//            System.out.println(collect.retainAll(userTaxons));
//
//
//            for (Long collectid : collect) {
//                for (Long usetaxonId : userTaxons)
//                    if (collectid.equals(usetaxonId)) {
//                        answer = true;
//
//                    } else {
//                        answer = false;
//                    }
//            }
////            return taxonDao.getUserTaxonsWithChildren(user).contains(collect);
//
////            List<Taxon>taxons = materialDao.findById(id).getTaxons();
////            List<Long>taxonIds = (List<Long>) taxons.stream().mapToLong(taxon1 -> taxon1.getId());
////            return taxonDao.getUserTaxonsWithChildren(user).contains(materialDao.findById(id).getTaxons());
//        } else {
//            answer = false;
//        }
//        return answer;
//    }
}
