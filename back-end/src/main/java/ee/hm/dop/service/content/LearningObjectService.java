package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.service.permission.PermissionItem;
import ee.hm.dop.service.permission.PermissionFactory;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.ValidatorUtil;

import javax.inject.Inject;

public class LearningObjectService {

    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private SolrEngineService solrEngineService;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = learningObjectDao.findById(learningObjectId);
        return canAccess(user, learningObject) ? learningObject : null;
    }

    public void incrementViewCount(LearningObject learningObject) {
        LearningObject originalPortfolio = validateAndFindIncludeDeleted(learningObject);
        learningObjectDao.incrementViewCount(originalPortfolio);
        solrEngineService.updateIndex();
    }

    public boolean canAccess(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canInteract(user, learningObject);
    }

    public boolean canView(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canView(user, learningObject);
    }

    public boolean canUpdate(User user, LearningObject learningObject) {
        if (learningObject == null) return false;
        return getLearningObjectHandler(learningObject).canUpdate(user, learningObject);
    }

    public LearningObject validateAndFind(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdNotDeleted);
    }

    public LearningObject validateAndFindIncludeDeleted(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findById);
    }

    public LearningObject validateAndFindDeletedOnly(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdDeleted);
    }

    private PermissionItem getLearningObjectHandler(LearningObject learningObject) {
        return PermissionFactory.get(learningObject.getClass());
    }
}
