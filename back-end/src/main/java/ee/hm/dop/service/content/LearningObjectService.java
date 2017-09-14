package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.UserFavoriteDao;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.dto.TagDTO;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.learningObject.LearningObjectHandler;
import ee.hm.dop.service.learningObject.LearningObjectHandlerFactory;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.metadata.TargetGroupService;
import ee.hm.dop.service.metadata.TaxonService;
import ee.hm.dop.service.solr.SolrEngineService;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

public class LearningObjectService {

    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private UserFavoriteDao userFavoriteDao;
    @Inject
    private MaterialDao materialDao;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private TaxonService taxonService;
    @Inject
    private TargetGroupService targetGroupService;
    @Inject
    private ResourceTypeService resourceTypeService;
    @Inject
    private ChangedLearningObjectService changedLearningObjectService;
    @Inject
    private TagService tagService;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = getLearningObjectDao().findById(learningObjectId);
        return hasPermissionsToAccess(user, learningObject) ? learningObject : null;
    }

    public boolean hasPermissionsToAccess(User user, LearningObject learningObject) {
        if (learningObject == null) {
            return false;
        }

        LearningObjectHandler learningObjectHandler = getLearningObjectHandler(learningObject);
        return learningObjectHandler.hasPermissionsToAccess(user, learningObject);
    }

    public LearningObject addTag(LearningObject learningObject, Tag tag, User user) {
        LearningObject updatedLearningObject;
        if (!hasPermissionsToAccess(user, learningObject)) {
            throw new RuntimeException("Access denied");
        }

        List<Tag> tags = learningObject.getTags();
        if (tags.contains(tag)) {
            throw new RuntimeException("Learning Object already contains tag");
        }

        tags.add(tag);
        updatedLearningObject = getLearningObjectDao().createOrUpdate(learningObject);
        solrEngineService.updateIndex();

        return updatedLearningObject;
    }

    public TagDTO addSystemTag(Long learningObjectId, String type, String tagName, User user) {
        LearningObject learningObject = getLearningObjectByType(learningObjectId, type);
        if (learningObject == null) {
            throw new NoSuchElementException();
        }

        Tag tag = tagService.getTagByName(tagName);
        if (tag == null) {
            tag = new Tag();
            tag.setName(tagName);
        }

        LearningObject newLearningObject = addTag(learningObject, tag, user);

        if (!hasPermissionsToAccess(user, learningObject)) {
            throw new RuntimeException("Access denied");
        }

        return getTagDTO(tagName, newLearningObject, user);
    }

    private TagDTO getTagDTO(String tagName, LearningObject learningObject, User user) {
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

        LearningObject updatedLearningObject = getLearningObjectDao().createOrUpdate(learningObject);
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

    private LearningObject getLearningObjectByType(Long learningObjectId, String type) {
        LearningObject learningObject = null;

        if (isMaterial(type)) {
            learningObject = materialDao.findById(learningObjectId);
        } else if (isPortfolio(type)) {
            learningObject = portfolioDao.findById(learningObjectId);
        }

        return learningObject;
    }

    private boolean isMaterial(String type) {
        return ".Material".equals(type) || ".ReducedMaterial".equals(type);
    }

    private boolean isPortfolio(String type) {
        return ".Portfolio".equals(type) || ".ReducedPortfolio".equals(type);
    }

    private List<LearningObject> getPublicLearningObjects(int numberOfLearningObjects,
                                                          BiFunction<Integer, Integer, List<LearningObject>> functionToGetLearningObjects) {
        List<LearningObject> returnableLearningObjects = new ArrayList<>();
        int startPosition = 0;
        int count = numberOfLearningObjects;
        while (returnableLearningObjects.size() != numberOfLearningObjects) {
            List<LearningObject> learningObjects = functionToGetLearningObjects.apply(count, startPosition);
            if (learningObjects.size() == 0) {
                break;
            }

            learningObjects.removeIf(learningObject -> !getLearningObjectHandler(learningObject).isPublic(learningObject));
            returnableLearningObjects.addAll(learningObjects);
            startPosition += count;
            count = numberOfLearningObjects - returnableLearningObjects.size();
        }

        return returnableLearningObjects;
    }

    List<LearningObject> getNewestLearningObjects(int numberOfLearningObjects) {
        return getPublicLearningObjects(numberOfLearningObjects, getLearningObjectDao()::findNewestLearningObjects);
    }

    LearningObjectHandler getLearningObjectHandler(LearningObject learningObject) {
        return LearningObjectHandlerFactory.get(learningObject.getClass());
    }

    LearningObjectDao getLearningObjectDao() {
        return learningObjectDao;
    }

    public UserFavorite addUserFavorite(LearningObject learningObject, User loggedInUser) {
        validateLearningObjectAndIdNotNull(learningObject);

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setAdded(DateTime.now());
        userFavorite.setCreator(loggedInUser);
        userFavorite.setLearningObject(learningObject);

        return userFavoriteDao.createOrUpdate(userFavorite);
    }

    public void removeUserFavorite(Long id, User loggedInUser) {
        LearningObject learningObject = learningObjectDao.findById(id);

        validateLearningObjectAndIdNotNull(learningObject);
        userFavoriteDao.deleteByLearningObjectAndUser(learningObject, loggedInUser);
    }

    public UserFavorite hasFavorited(Long id, User loggedInUser) {
        if(id == null || loggedInUser == null) return null;
        return userFavoriteDao.findFavoriteByUserAndLearningObject(id, loggedInUser);
    }

    public List<ReducedLearningObject> getUserFavorites(User loggedInUser, int start, int maxResult) {
        return userFavoriteDao.findUsersFavoritedLearningObjects(loggedInUser, start, maxResult);
    }

    private void validateLearningObjectAndIdNotNull(LearningObject learningObject) {
        if (learningObject == null || learningObject.getId() == null) {
            throw new RuntimeException("LearningObject not found");
        }
    }

    public long getUserFavoritesSize(User loggedInUser) {
        return userFavoriteDao.findUsersFavoritedLearningObjectsCount(loggedInUser);
    }
}
