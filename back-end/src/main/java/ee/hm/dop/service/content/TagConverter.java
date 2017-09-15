package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.dto.TagDTO;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.metadata.TargetGroupService;
import ee.hm.dop.service.metadata.TaxonService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.LearningObjectUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

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
    private ChangedLearningObjectService changedLearningObjectService;
    @Inject
    private LearningObjectService learningObjectService;

    public TagDTO getTagDTO(String tagName, LearningObject learningObject, User user) {
        TagDTO tagDTO = new TagDTO();

        ChangedLearningObject changedLearningObject = new ChangedLearningObject();
        changedLearningObject.setLearningObject(learningObject);
        changedLearningObject.setChanger(user);
        changedLearningObject.setAdded(DateTime.now());

        Taxon taxon = getTaxonByTranslation(tagName);
        ResourceType resourceType = resourceTypeService.findResourceByTranslation(tagName);
        TargetGroup targetGroup = targetGroupService.getByTranslation(tagName);

        boolean hasChanged = false;
        if (taxon != null) {
            addTaxon(learningObject, taxon);
            changedLearningObject.setTaxon(taxon);
            tagDTO.setTagTypeName("taxon");
            hasChanged = true;
        } else if (learningObject instanceof Material && resourceType != null) {
            addResourceType((Material) learningObject, resourceType);
            changedLearningObject.setResourceType(resourceType);
            tagDTO.setTagTypeName("resourcetype");
            hasChanged = true;
        } else if (targetGroup != null) {
            addTargetGroup(targetGroup, learningObject);
            changedLearningObject.setTargetGroup(targetGroup);
            tagDTO.setTagTypeName("targetgroup");
            hasChanged = true;
        }

        changedLearningObjectService.addChanged(changedLearningObject);

        LearningObject updatedLearningObject = learningObjectService.getLearningObjectDao().createOrUpdate(learningObject);
        updatedLearningObject.setChanged(hasChanged ? (updatedLearningObject.getChanged() + 1) : updatedLearningObject.getChanged());
        tagDTO.setLearningObject(updatedLearningObject);

        solrEngineService.updateIndex();

        return tagDTO;
    }

    private void addResourceType(Material learningObject, ResourceType resourceType) {
        learningObject.getResourceTypes().add(resourceType);
    }

    private void addTargetGroup(TargetGroup targetGroup, LearningObject learningObject) {
        learningObject.getTargetGroups().add(targetGroup);
    }

    private void addTaxon(LearningObject learningObject, Taxon taxon) {
        List<Taxon> learningObjectTaxons = learningObject.getTaxons();
        if (learningObjectTaxons != null) {
            learningObjectTaxons.add(taxon);
        }
    }

    private Taxon getTaxonByTranslation(String tagName) {
        return taxonService.findTaxonByTranslation(tagName);
    }

}
