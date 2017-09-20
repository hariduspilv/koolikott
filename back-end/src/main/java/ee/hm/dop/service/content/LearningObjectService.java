package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.service.content.dto.TagDTO;
import ee.hm.dop.model.User;
import ee.hm.dop.service.learningObject.PermissionItem;
import ee.hm.dop.service.learningObject.PermissionFactory;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.LearningObjectUtils;
import ee.hm.dop.utils.ValidatorUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LearningObjectService {

    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private MaterialDao materialDao;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private TagService tagService;
    @Inject
    private TagConverter tagConverter;

    public LearningObject get(long learningObjectId, User user) {
        LearningObject learningObject = getLearningObjectDao().findById(learningObjectId);
        return canAcess(user, learningObject) ? learningObject : null;
    }

    public boolean canAcess(User user, LearningObject learningObject) {
        if (learningObject == null) {
            return false;
        }
        return getLearningObjectHandler(learningObject).canAccess(user, learningObject);
    }

    public LearningObject validateAndFind(LearningObject learningObject) {
        return ValidatorUtil.findValid(learningObject, learningObjectDao::findByIdNotDeleted);
    }

    public LearningObject addTag(LearningObject learningObject, Tag tag, User user) {
        if (!canAcess(user, learningObject)) {
            throw new RuntimeException("Access denied");
        }

        List<Tag> tags = learningObject.getTags();
        if (tags.contains(tag)) {
            throw new RuntimeException("Learning Object already contains tag");
        }

        tags.add(tag);
        LearningObject updatedLearningObject = getLearningObjectDao().createOrUpdate(learningObject);
        solrEngineService.updateIndex();

        return updatedLearningObject;
    }

    //todo this method doesn't make sense, as type information is lost in return value
    private LearningObject getLearningObjectByType(Long learningObjectId, String type) {
        if (LearningObjectUtils.isMaterial(type)) {
            return materialDao.findById(learningObjectId);
        }
        if (LearningObjectUtils.isPortfolio(type)) {
            return portfolioDao.findById(learningObjectId);
        }
        return null;
    }

    public TagDTO addSystemTag(Long learningObjectId, String type, String tagName, User user) {
        LearningObject learningObject = getLearningObjectByType(learningObjectId, type);
        ValidatorUtil.mustHaveEntity(learningObject);

        Tag tag = findOrMakeNewTag(tagName);
        LearningObject newLearningObject = addTag(learningObject, tag, user);
        return tagConverter.getTagDTO(tagName, newLearningObject, user);
    }

    private Tag findOrMakeNewTag(String tagName) {
        Tag tag = tagService.getTagByName(tagName);
        if (tag != null) {
            return tag;
        }
        Tag newTag = new Tag();
        newTag.setName(tagName);
        return newTag;
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

    PermissionItem getLearningObjectHandler(LearningObject learningObject) {
        return PermissionFactory.get(learningObject.getClass());
    }

    LearningObjectDao getLearningObjectDao() {
        return learningObjectDao;
    }
}
