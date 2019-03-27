package ee.hm.dop.service.content;

import ee.hm.dop.dao.AdminLearningObjectDao;
import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.interfaces.IMaterial;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class LearningObjectAdministrationService {

    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private ReviewManager reviewManager;
    @Inject
    private AdminLearningObjectDao adminLearningObjectDao;
    @Inject
    private PortfolioMaterialService portfolioMaterialService;

    public Recommendation addRecommendation(LearningObject learningObject, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(LocalDateTime.now());
        originalLearningObject.setRecommendation(recommendation);

        originalLearningObject = learningObjectDao.createOrUpdate(originalLearningObject);
        solrEngineService.updateIndex();

        return originalLearningObject.getRecommendation();
    }

    public void removeRecommendation(LearningObject learningObject, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);

        LearningObject originalPortfolio = learningObjectService.validateAndFind(learningObject);
        originalPortfolio.setRecommendation(null);

        learningObjectDao.createOrUpdate(originalPortfolio);
        solrEngineService.updateIndex();
    }

    public LearningObject restore(LearningObject learningObject, User user) {
        UserUtil.mustBeAdmin(user);
        LearningObject originalLearningObject = learningObjectService.validateAndFindDeletedOnly(learningObject);

        learningObjectDao.restore(originalLearningObject);
        reviewManager.setEverythingReviewed(user, originalLearningObject, ReviewStatus.RESTORED, ReviewType.SYSTEM_RESTORE);
        solrEngineService.updateIndex();
        if (originalLearningObject instanceof Portfolio) {
            portfolioMaterialService.save((Portfolio) originalLearningObject);
        }
        return originalLearningObject;
    }

    public LearningObject delete(LearningObject learningObject, User loggedInUser) {
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);

        if (originalLearningObject instanceof IMaterial) {
            UserUtil.mustBeModeratorOrAdmin(loggedInUser);
        } else {
            if (!learningObjectService.canUpdate(loggedInUser, originalLearningObject)) {
                throw ValidatorUtil.permissionError();
            }
        }

        learningObjectDao.delete(originalLearningObject);
        reviewManager.setEverythingReviewed(loggedInUser, originalLearningObject, ReviewStatus.DELETED, ReviewType.SYSTEM_DELETE);
        solrEngineService.updateIndex();
        if (originalLearningObject instanceof Portfolio) {
            portfolioMaterialService.delete((Portfolio) originalLearningObject);
        }
        return originalLearningObject;
    }

    public List<AdminLearningObject> findByIdDeleted() {
        return adminLearningObjectDao.findByIdDeleted();
    }

    public Long findCountByIdDeleted() {
        return adminLearningObjectDao.findCountByIdDeleted();
    }
}
