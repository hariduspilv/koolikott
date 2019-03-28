package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.dto.TagDTO;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.metadata.TargetGroupService;
import ee.hm.dop.service.metadata.TaxonService;
import ee.hm.dop.service.reviewmanagement.ChangeProcessStrategy;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import ee.hm.dop.service.solr.SolrEngineService;

import javax.inject.Inject;

public class TagConverter {

    public static final String TAXON = "taxon";
    public static final String RESOURCETYPE = "resourcetype";
    public static final String TARGETGROUP = "targetgroup";

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
        Taxon taxon = taxonService.findTaxonByTranslation(tagName);
        ResourceType resourceType = resourceTypeService.findResourceByTranslation(tagName);
        TargetGroup targetGroup = targetGroupService.getByTranslation(tagName);

        boolean changed = hasChanged(learningObject, taxon, resourceType, targetGroup);
        String tagTypeName = tagName(taxon, resourceType, targetGroup);
        if (!changed) {
            return convertDto(learningObject, tagTypeName);
        }

        if (ChangeProcessStrategy.processStrategy(learningObject).processNewChanges()) {
            reviewableChangeService.registerChange(learningObject, user, taxon, resourceType, targetGroup, null);
            learningObject.setChanged(learningObject.getChanged() + 1);
        }
        LearningObject updatedLearningObject = learningObjectDao.createOrUpdate(learningObject);
        solrEngineService.updateIndex();
        return convertDto(updatedLearningObject, tagTypeName);
    }

    private TagDTO convertDto(LearningObject updatedLearningObject, String tagTypeName) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setLearningObject(updatedLearningObject);
        tagDTO.setTagTypeName(tagTypeName);
        return tagDTO;
    }

    private boolean hasChanged(LearningObject learningObject, Taxon taxon, ResourceType resourceType, TargetGroup targetGroup) {
        if (resourceType != null) {
            return learningObject instanceof Material;
        }
        return taxon != null || targetGroup != null;
    }

    private String tagName(Taxon taxon, ResourceType resourceType, TargetGroup targetGroup) {
        if (taxon != null) {
            return TAXON;
        }
        if (resourceType != null) {
            return RESOURCETYPE;
        }
        if (targetGroup != null) {
            return TARGETGROUP;
        }
        return null;
    }
}
