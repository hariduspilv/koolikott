package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
<<<<<<< HEAD
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
=======
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.interfaces.IMaterial;
import ee.hm.dop.service.reviewmanagement.BrokenContentService;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.reviewmanagement.ImproperContentAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
>>>>>>> new-develop
import org.joda.time.DateTime;

import javax.inject.Inject;

public class LearningObjectAdministrationService {

    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private SolrEngineService solrEngineService;
<<<<<<< HEAD
=======
    @Inject
    private ReviewManager reviewManager;
>>>>>>> new-develop

    public Recommendation addRecommendation(LearningObject learningObject, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(DateTime.now());
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
<<<<<<< HEAD
=======

    public void restore(LearningObject learningObject, User user) {
        UserUtil.mustBeAdmin(user);
        LearningObject originalLearningObject = learningObjectService.validateAndFindDeletedOnly(learningObject);

        learningObjectDao.restore(originalLearningObject);
        reviewManager.setEverythingReviewed(user, originalLearningObject, ReviewStatus.RESTORED);
        solrEngineService.updateIndex();
    }


    public void delete(LearningObject learningObject, User loggedInUser) {
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);

        if (learningObject instanceof IMaterial) {
            UserUtil.mustBeModeratorOrAdmin(loggedInUser);
        } else {
            if (!learningObjectService.canUpdate(loggedInUser, originalLearningObject)) {
                throw ValidatorUtil.permissionError();
            }
        }

        learningObjectDao.delete(originalLearningObject);
        reviewManager.setEverythingReviewed(loggedInUser, originalLearningObject, ReviewStatus.DELETED);
        solrEngineService.updateIndex();
    }
>>>>>>> new-develop
}
