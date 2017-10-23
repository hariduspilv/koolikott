package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
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

    public List<ReviewableChange> getAllByLearningObject(long id) {
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

    @Deprecated
    public ReviewableChange addChanged(ReviewableChange reviewableChange) {
        findValid(reviewableChange);

        if (!reviewableChange.hasChange()) {
            return null;
        }

        return reviewableChangeDao.createOrUpdate(reviewableChange);
    }

    private void findValid(ReviewableChange reviewableChange) {
        LearningObject learningObject = learningObjectService.get(reviewableChange.getLearningObject().getId(), reviewableChange.getCreatedBy());
        ValidatorUtil.mustHaveEntity(learningObject);
    }

    public boolean acceptAllChanges(long id) {
        return reviewableChangeDao.removeAllByLearningObject(id);
    }

    public LearningObject revertAllChanges(long id, User user) {
        LearningObject learningObject = learningObjectService.get(id, user);
        ValidatorUtil.mustHaveEntity(learningObject);
        List<ReviewableChange> reviewableChanges = reviewableChangeDao.getAllByLearningObject(id);
        if (CollectionUtils.isEmpty(reviewableChanges)) {
            throw new RuntimeException("No changes for this learningObject");
        }

        for (ReviewableChange change : reviewableChanges) {
            if (change.getTaxon() != null) {
                removeTaxonFromLearningObject(learningObject, change.getTaxon());
            } else if (change.getResourceType() != null) {
                removeResourceTypeFromLearningObject(learningObject, change.getResourceType());
            } else if (change.getTargetGroup() != null) {
                removeTargetGroupFromLearningObject(learningObject, change.getTargetGroup());
            }
        }

        boolean success = acceptAllChanges(id);
        LearningObject updatedLearningObject = learningObjectDao.createOrUpdate(learningObject);
        if (success) {
            updatedLearningObject.setChanged(0);
        }

        return updatedLearningObject;
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

    public void removeChangeById(long id) {
        reviewableChangeDao.removeById(id);
    }

    public boolean learningObjectHasThis(LearningObject learningObject, ReviewableChange change) {
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
}
