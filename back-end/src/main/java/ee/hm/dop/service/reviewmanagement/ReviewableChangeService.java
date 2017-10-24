package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.taxon.Taxon;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

public class ReviewableChangeService {

    @Inject
    private ReviewableChangeDao reviewableChangeDao;
    @Inject
    private ReviewableChangeAdminService reviewableChangeAdminService;

    public List<ReviewableChange> getAllByLearningObject(Long id) {
        return reviewableChangeDao.getAllByLearningObject(id);
    }

    public ReviewableChange registerChange(LearningObject learningObject, User user, Taxon taxon, ResourceType resourceType, TargetGroup targetGroup, String materialSource) {
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
        } else {
            if (materialSource != null) {
                reviewableChange.setMaterialSource(materialSource);
            }
        }
        if (reviewableChange.hasChange()) {
            return reviewableChangeDao.createOrUpdate(reviewableChange);
        }
        return null;
    }

    public void processChanges(LearningObject learningObject, User user, String sourceBefore) {
        if (learningObject instanceof Material) {
            Material material = (Material) learningObject;
            if (!Objects.equals(material.getSource(), sourceBefore)) {
                if (sourceChangeDoesntExist(material)) {
                    registerChange(learningObject, user, null, null, null, sourceBefore);
                }
            }
        }
        for (ReviewableChange change : learningObject.getReviewableChanges()) {
            if (!change.isReviewed() && change.getMaterialSource() == null) {
                if (!learningObjectHasThis(learningObject, change)) {
                    reviewableChangeAdminService.setReviewed(change, user, ReviewStatus.OBSOLETE);
                }
            }
        }
    }

    private boolean sourceChangeDoesntExist(Material material) {
        return material.getReviewableChanges().stream()
                .filter(r -> !r.isReviewed())
                .noneMatch(r -> r.getMaterialSource() != null);
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
}
