package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.permission.PermissionFactory;
import ee.hm.dop.service.permission.PermissionItem;
import ee.hm.dop.utils.ValidatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.UserUtil.isAdmin;
import static ee.hm.dop.utils.UserUtil.isModerator;

@Service
@Transactional
public class LearningObjectService {

    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private PermissionFactory permissionFactory;
    @Inject
    private LearningObjectServiceCache learningObjectServiceCache;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = learningObjectDao.findById(learningObjectId);
        return canAccess(user, learningObject) ? learningObject : null;
    }

    public void incrementViewCount(LearningObject learningObject) {
        LearningObject originalLearningObject = validateAndFindIncludeDeleted(learningObject);
        learningObjectDao.incrementViewCount(originalLearningObject);
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
        return permissionFactory.get(learningObject);
    }

    public boolean showUnreviewed(Long id, User user) {
        if (isAdmin(user)) {
            return true;
        } else if (isModerator(user)) {
            LearningObject obj = learningObjectDao.findById(id);
            if (obj == null) return false;
            List<Long> collect = obj.getTaxons().stream().map(Taxon::getId).collect(Collectors.toList());
            List<Long> userTaxons = learningObjectServiceCache.getUserTaxonWithChildren(user.getId());
            return userTaxons != null && collect.stream().anyMatch(userTaxons::contains);
        }
        return false;
    }

    public List<LearningObject> getAllByCreator(User creator) {
        return learningObjectDao.findAllByCreator(creator);
    }
}
