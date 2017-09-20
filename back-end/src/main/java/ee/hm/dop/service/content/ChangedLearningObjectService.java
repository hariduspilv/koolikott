package ee.hm.dop.service.content;

import ee.hm.dop.dao.ChangedLearningObjectDao;
import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

public class ChangedLearningObjectService {

    @Inject
    private ChangedLearningObjectDao changedLearningObjectDao;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private LearningObjectService learningObjectService;

    public long getCount() {
        return changedLearningObjectDao.getCount();
    }

    public List<ChangedLearningObject> findAll() {
        return changedLearningObjectDao.findAll();
    }

    public List<ChangedLearningObject> getAllByLearningObject(long id) {
        return changedLearningObjectDao.getAllByLearningObject(id);
    }

    ChangedLearningObject addChanged(ChangedLearningObject changedLearningObject) {
        findValid(changedLearningObject);

        if (!hasChange(changedLearningObject)) {
            return null;
        }

        return changedLearningObjectDao.createOrUpdate(changedLearningObject);
    }

    private void findValid(ChangedLearningObject changedLearningObject) {
        notNullnotNullId(changedLearningObject);
        LearningObject learningObject = learningObjectService.get(changedLearningObject.getLearningObject().getId(), changedLearningObject.getChanger());
        ValidatorUtil.mustHaveEntity(learningObject);
    }

    private void notNullnotNullId(ChangedLearningObject changedLearningObject) {
        if (changedLearningObject == null || changedLearningObject.getLearningObject() == null) {
            throw new RuntimeException("Invalid changed learningObject");
        }
    }

    private void notNull(LearningObject learningObject) {
        if (learningObject == null) {
            throw new RuntimeException("LearningObject does not exists.");
        }
    }

    private boolean hasChange(ChangedLearningObject changedLearningObject) {
        return changedLearningObject.getTaxon() != null || changedLearningObject.getResourceType() != null || changedLearningObject.getTargetGroup() != null;
    }

    public boolean acceptAllChanges(long id) {
        return changedLearningObjectDao.removeAllByLearningObject(id);
    }

    public LearningObject revertAllChanges(long id, User user) {
        LearningObject learningObject = learningObjectService.get(id, user);
        notNull(learningObject);
        List<ChangedLearningObject> changedLearningObjects = changedLearningObjectDao.getAllByLearningObject(id);
        if (CollectionUtils.isEmpty(changedLearningObjects)) {
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
        changedLearningObjectDao.removeById(id);
    }

    boolean learningObjectHasThis(LearningObject learningObject, ChangedLearningObject change) {
        if (change.getTaxon() != null) {
            return learningObject.getTaxons() != null && learningObject.getTaxons().contains(change.getTaxon());
        }  else if (change.getTargetGroup() != null) {
            return learningObject.getTargetGroups() != null && learningObject.getTargetGroups().contains(change.getTargetGroup());
        } else if (change.getResourceType() != null && learningObject instanceof Material) {
            Material material = (Material) learningObject;
            return material.getResourceTypes() != null && material.getResourceTypes().contains(change.getResourceType());
        }
        return false;
    }
}
