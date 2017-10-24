package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

public class ReviewableChangeService {

    @Inject
    private ReviewableChangeDao reviewableChangeDao;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private ReviewableChangeAdminService reviewableChangeAdminService;
    @Inject
    private ReviewManager reviewManager;

    public List<ReviewableChange> getAllByLearningObject(Long id) {
        return reviewableChangeDao.getAllByLearningObject(id);
    }

    public void registerChange(LearningObject learningObject, User user, Taxon taxon, ResourceType resourceType, TargetGroup targetGroup) {
        ReviewableChange reviewableChange = new ReviewableChange();
        reviewableChange.setLearningObject(learningObject);
        reviewableChange.setCreatedBy(user);
        reviewableChange.setCreatedAt(DateTime.now());
        reviewableChange.setReviewed(false);
        if (taxon != null) {
            reviewableChange.setTaxon(taxon);
        } else if (resourceType != null) {
            if (learningObject instanceof Material) {
                reviewableChange.setResourceType(resourceType);
            }
        } else if (targetGroup != null) {
            reviewableChange.setTargetGroup(targetGroup);
        }
        if (reviewableChange.hasChange()) {
            reviewableChangeDao.createOrUpdate(reviewableChange);
        }
    }

    public void processChanges(LearningObject learningObject, User user) {
        //todo register logic
        List<ReviewableChange> changes = learningObject.getReviewableChanges();
        //todo remove isNotEmty check
        if (CollectionUtils.isNotEmpty(changes)) {
            for (ReviewableChange change : changes) {
                if (!change.isReviewed()) {
                    if (!learningObjectHasThis(learningObject, change)) {
                        //todo do something, but some u set reviewed, some u create a new
//                        reviewableChangeAdminService.setReviewed(change, user, ReviewStatus.OBSOLETE);
                    }
                }
            }
        }
    }

    boolean learningObjectHasThis(LearningObject learningObject, ReviewableChange change) {
        if (change.getTaxon() != null) {
            return learningObject.getTaxons() != null && learningObject.getTaxons().contains(change.getTaxon());
        } else if (change.getTargetGroup() != null) {
            return learningObject.getTargetGroups() != null && learningObject.getTargetGroups().contains(change.getTargetGroup());
        } else if (change.getResourceType() != null && learningObject instanceof Material) {
            Material material = (Material) learningObject;
            return material.getResourceTypes() != null && material.getResourceTypes().contains(change.getResourceType());
        }
        return false;
    }

    @Deprecated
    public ReviewableChange addChanged(ReviewableChange reviewableChange) {
        return reviewableChange.hasChange() ? reviewableChangeDao.createOrUpdate(reviewableChange) : null;
    }

    public LearningObject revertAllChanges(Long learningObjectId, User user) {
        LearningObject learningObject = learningObjectService.get(learningObjectId, user);
        ValidatorUtil.mustHaveEntity(learningObject);
        List<ReviewableChange> reviewableChanges = learningObject.getReviewableChanges();

        for (ReviewableChange change : reviewableChanges) {
            if (change.getTaxon() != null) {
                removeTaxonFromLearningObject(learningObject, change.getTaxon());
            } else if (change.getResourceType() != null) {
                removeResourceTypeFromLearningObject(learningObject, change.getResourceType());
            } else if (change.getTargetGroup() != null) {
                removeTargetGroupFromLearningObject(learningObject, change.getTargetGroup());
            }
        }

        reviewManager.setEverythingReviewed(user, learningObject, ReviewStatus.REJECTED, ReviewType.CHANGE);
        return learningObjectDao.createOrUpdate(learningObject);
    }

    private void removeTargetGroupFromLearningObject(LearningObject learningObject, TargetGroup targetGroup) {
        Iterator iterator = learningObject.getTargetGroups().iterator();
        while (iterator.hasNext()) {
            TargetGroup tg = (TargetGroup) iterator.next();
            if (tg.getId().equals(targetGroup.getId())) {
                iterator.remove();
            }
        }
    }

    private void removeResourceTypeFromLearningObject(LearningObject learningObject, ResourceType resourceType) {
        if (learningObject instanceof Material) {
            Iterator iterator = ((Material) learningObject).getResourceTypes().iterator();
            while (iterator.hasNext()) {
                ResourceType rt = (ResourceType) iterator.next();
                if (rt.getId().equals(resourceType.getId())) {
                    iterator.remove();
                }
            }
        }
    }

    private void removeTaxonFromLearningObject(LearningObject learningObject, Taxon taxon) {
        Iterator iterator = learningObject.getTaxons().iterator();
        while (iterator.hasNext()) {
            Taxon t = (Taxon) iterator.next();
            if (t.getId().equals(taxon.getId())) {
                iterator.remove();
            }
        }
    }
}
