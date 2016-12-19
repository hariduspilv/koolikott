package ee.hm.dop.service;

import ee.hm.dop.dao.ChangedLearningObjectDAO;
import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

public class ChangedLearningObjectService {

    @Inject
    private ChangedLearningObjectDAO changedLearningObjectDAO;

    @Inject
    private LearningObjectDAO learningObjectDAO;

    @Inject
    private LearningObjectService learningObjectService;

    public long getCount() {
        return changedLearningObjectDAO.getCount();
    }

    public List<ChangedLearningObject> findAll() {
        return changedLearningObjectDAO.findAll();
    }

    public List<ChangedLearningObject> getAllByLearningObject(long id) {
        return changedLearningObjectDAO.getAllByLearningObject(id);
    }

    ChangedLearningObject addChanged(ChangedLearningObject changedLearningObject) {
        if (changedLearningObject == null || changedLearningObject.getLearningObject() == null) {
            throw new RuntimeException("Invalid changed learningObject");
        }

        LearningObject learningObject = learningObjectService
                .get(changedLearningObject.getLearningObject().getId(), changedLearningObject.getChanger());

        if (learningObject == null) {
            throw new RuntimeException("LearningObject does not exists.");
        }

        if (!hasChange(changedLearningObject)) {
            return null;
        }

        return changedLearningObjectDAO.update(changedLearningObject);
    }

    private boolean hasChange(ChangedLearningObject changedLearningObject) {
        return changedLearningObject.getTaxon() != null || changedLearningObject.getResourceType() != null || changedLearningObject.getTargetGroup() != null;
    }

    public boolean acceptAllChanges(long id) {
        return changedLearningObjectDAO.removeAllByLearningObject(id);
    }

    public LearningObject revertAllChanges(long id, User user) {
        LearningObject learningObject = learningObjectService.get(id, user);
        if (learningObject == null) {
            throw new RuntimeException("LearningObject does not exists.");
        }
        List<ChangedLearningObject> changedLearningObjects = changedLearningObjectDAO.getAllByLearningObject(id);

        if (changedLearningObjects == null || changedLearningObjects.isEmpty()) {
            throw new RuntimeException("No changes for this learningObject");
        }

        for (ChangedLearningObject change : changedLearningObjects) {
            if (change.getTaxon() != null) {
                removeTaxonFromLearningObject(learningObject, change.getTaxon());
            } else if (change.getResourceType() != null) {
                removeResourceTypeFromLearningObject(learningObject, change.getResourceType());
            } else if (change.getTargetGroup() != null) {
                removeTargetGroupFromLearningObject(learningObject, change.getTargetGroup());
            }
        }

        boolean success = acceptAllChanges(id);
        LearningObject updatedLearningObject = learningObjectDAO.update(learningObject);
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
        if (learningObject instanceof Portfolio) {
            Iterator iterator = ((Portfolio) learningObject).getTaxons().iterator();
            while (iterator.hasNext()) {
                Taxon t = (Taxon) iterator.next();
                if (t.getId().equals(taxon.getId())) {
                    iterator.remove();
                }
            }
        } else if (learningObject instanceof Material) {
            Iterator iterator = ((Material) learningObject).getTaxons().iterator();
            while (iterator.hasNext()) {
                Taxon t = (Taxon) iterator.next();
                if (t.getId().equals(taxon.getId())) {
                    iterator.remove();
                }
            }
        }
    }

    public boolean removeChangeById(long id) {
        return changedLearningObjectDAO.removeById(id);
    }

    public boolean materialHasThis(Material material, ChangedLearningObject change) {
        if (change.getTaxon() != null) {
            return material.getTaxons() != null && material.getTaxons().contains(change.getTaxon());
        } else if (change.getResourceType() != null) {
            return material.getResourceTypes() != null && material.getResourceTypes().contains(change.getResourceType());
        } else if (change.getTargetGroup() != null) {
            return material.getTargetGroups() != null && material.getTargetGroups().contains(change.getTargetGroup());
        }

        return false;
    }

    public boolean portfolioHasThis(Portfolio portfolio, ChangedLearningObject change) {
        if (change.getTaxon() != null) {
            return portfolio.getTaxons() != null && portfolio.getTaxons().contains(change.getTaxon());
        }  else if (change.getTargetGroup() != null) {
            return portfolio.getTargetGroups() != null && portfolio.getTargetGroups().contains(change.getTargetGroup());
        }

        return false;
    }
}
