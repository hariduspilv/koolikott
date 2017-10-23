package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.dto.TagDTO;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.metadata.TargetGroupService;
import ee.hm.dop.service.metadata.TaxonService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import ee.hm.dop.service.solr.SolrEngineService;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class TagConverter {
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private TaxonService taxonService;
    @Inject
    private TargetGroupService targetGroupService;
    @Inject
    private ResourceTypeService resourceTypeService;
    @Inject
    private ReviewableChangeService reviewableChangeService;
    @Inject
    private LearningObjectDao learningObjectDao;

    public TagDTO addChangeReturnTagDto(String tagName, LearningObject learningObject, User user) {
        ReviewableChange reviewableChange = new ReviewableChange();
        reviewableChange.setLearningObject(learningObject);
        reviewableChange.setCreatedBy(user);
        reviewableChange.setCreatedAt(DateTime.now());
        reviewableChange.setReviewed(false);

        Taxon taxon = taxonService.findTaxonByTranslation(tagName);
        ResourceType resourceType = resourceTypeService.findResourceByTranslation(tagName);
        TargetGroup targetGroup = targetGroupService.getByTranslation(tagName);

        if (taxon != null) {
            learningObject.getTaxons().add(taxon);
        } else if (resourceType != null) {
            if (learningObject instanceof Material) {
                Material material = (Material) learningObject;
                material.getResourceTypes().add(resourceType);
            }
        } else if (targetGroup != null) {
            learningObject.getTargetGroups().add(targetGroup);
        }

        if (taxon != null) {
            reviewableChange.setTaxon(taxon);
        } else if (resourceType != null) {
            if (learningObject instanceof Material) {
                reviewableChange.setResourceType(resourceType);
            }
        } else if (targetGroup != null) {
            reviewableChange.setTargetGroup(targetGroup);
        }

        boolean hasChanged = reviewableChange.hasChange();

        if (learningObject.getUnReviewed() == 0) {
            reviewableChangeService.addChanged(reviewableChange);
        }

        LearningObject updatedLearningObject = learningObjectDao.createOrUpdate(learningObject);
        updatedLearningObject.setChanged(hasChanged ? updatedLearningObject.getChanged() + 1 : updatedLearningObject.getChanged());

        solrEngineService.updateIndex();

        return convertDto(updatedLearningObject, reviewableChange.tagName());
    }

    private TagDTO convertDto(LearningObject updatedLearningObject, String tagTypeName) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setLearningObject(updatedLearningObject);
        tagDTO.setTagTypeName(tagTypeName);
        return tagDTO;
    }

}
