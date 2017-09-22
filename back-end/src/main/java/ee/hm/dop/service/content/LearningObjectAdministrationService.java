package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class LearningObjectAdministrationService {

    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private SolrEngineService solrEngineService;

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
}
