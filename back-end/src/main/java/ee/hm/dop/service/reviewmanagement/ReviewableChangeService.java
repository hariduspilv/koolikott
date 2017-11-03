package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.taxon.Taxon;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

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
            learningObject.setChanged(learningObject.getChanged() + 1);
            ReviewableChange newChange = reviewableChangeDao.createOrUpdate(reviewableChange);
            learningObject.getReviewableChanges().add(newChange);
            return newChange;
        }
        return null;
    }

    public void processChanges(Portfolio material, User user, ChangeProcessStrategy changeProcessStrategy) {
        processChanges(material, user, null, changeProcessStrategy);
    }

    public void processChanges(Material material, User user, String sourceBefore, ChangeProcessStrategy changeProcessStrategy) {
        processChanges((LearningObject) material, user, sourceBefore, changeProcessStrategy);
    }

    private void processChanges(LearningObject learningObject, User user, String materialSourceBefore, ChangeProcessStrategy changeProcessStrategy) {
        boolean linkWasAddedBefore = true;
        if (changeProcessStrategy.processNewChanges()) {
            if (learningObject instanceof Material) {
                Material material = (Material) learningObject;
                if (urlHasChanged(materialSourceBefore, material)) {
                    Optional<ReviewableChange> sourceChangeOp = getSourceChange(material);
                    if (!sourceChangeOp.isPresent()) {
                        linkWasAddedBefore = false;
                        registerChange(learningObject, user, null, null, null, materialSourceBefore);
                    }
                }
            }
        }
        if (isNotEmpty(learningObject.getReviewableChanges())) {
            for (ReviewableChange change : learningObject.getReviewableChanges()) {
                if (!change.isReviewed()) {
                    if (change.getMaterialSource() == null && !learningObjectHasThis(learningObject, change)) {
                        reviewableChangeAdminService.setReviewed(change, user, ReviewStatus.OBSOLETE);
                        learningObject.setChanged(learningObject.getChanged() - 1);
                    }
                    if (linkWasAddedBefore && change.getMaterialSource() != null && learningObject instanceof Material && change.getMaterialSource().equals(((Material) learningObject).getSource())) {
                        reviewableChangeAdminService.setReviewed(change, user, ReviewStatus.OBSOLETE);
                        learningObject.setChanged(learningObject.getChanged() - 1);
                    }
                }
            }
        }
    }

    private boolean urlHasChanged(String materialSourceBefore, Material material) {
        return !Objects.equals(material.getSource(), materialSourceBefore);
    }

    private Optional<ReviewableChange> getSourceChange(Material material) {
        if (isEmpty(material.getReviewableChanges())) {
            return Optional.empty();
        }
        return material.getReviewableChanges().stream()
                .filter(r -> !r.isReviewed())
                .filter(r -> r.getMaterialSource() != null)
                .findAny();
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
