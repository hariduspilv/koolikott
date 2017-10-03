package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.interfaces.IMaterial;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class LearningObjectAdministrationService {

    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private FirstReviewService firstReviewService;
    @Inject
    private ImproperContentService improperContentService;
    @Inject
    private MaterialAdministrationService materialAdministrationService;

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

    public void restore(LearningObject learningObject, User user) {
        if (learningObject instanceof Material) {
            UserUtil.mustBeAdmin(user);
        } else {
            UserUtil.mustBeModeratorOrAdmin(user);
        }
        LearningObject originalLearningObject = learningObjectService.validateAndFindDeletedOnly(learningObject);

        learningObjectDao.restore(originalLearningObject);
        firstReviewService.setReviewed(originalLearningObject, user);
        improperContentService.deleteAll(originalLearningObject, user);
        if (originalLearningObject instanceof Material) {
            materialAdministrationService.setMaterialNotBroken((Material) originalLearningObject, user);
        }
        solrEngineService.updateIndex();
    }
}
