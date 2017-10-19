package ee.hm.dop.service.metadata;

import javax.inject.Inject;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.TagDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.User;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.content.TagConverter;
import ee.hm.dop.service.content.dto.TagDTO;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.ValidatorUtil;

import java.util.List;

public class TagService {

    @Inject
    private TagDao tagDao;
    @Inject
    private LearningObjectDao learningObjectDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private TagService tagService;
    @Inject
    private TagConverter tagConverter;
    @Inject
    private LearningObjectService learningObjectService;

    public Tag getTagByName(String name) {
        return tagDao.findByName(name);
    }

    public LearningObject addRegularTag(Long learningObjectId, String tagName, User loggedInUser) {
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);
        ValidatorUtil.mustHaveEntity(learningObject);

        return addTag(learningObject, findOrMakeNewTag(tagName), loggedInUser);
    }

    public TagDTO addSystemTag(Long learningObjectId, String tagName, User user) {
        LearningObject learningObject = learningObjectDao.findById(learningObjectId);
        ValidatorUtil.mustHaveEntity(learningObject);

        LearningObject newLearningObject = addTag(learningObject, findOrMakeNewTag(tagName), user);
        //todo why does system tag return tag dto
        return tagConverter.addChangeReturnTagDto(tagName, newLearningObject, user);
    }

    private LearningObject addTag(LearningObject learningObject, Tag newTag, User user) {
<<<<<<< HEAD
        if (!learningObjectService.canAcess(user, learningObject)) {
=======
        if (!learningObjectService.canAccess(user, learningObject)) {
>>>>>>> new-develop
            throw ValidatorUtil.permissionError();
        }

        List<Tag> tags = learningObject.getTags();
        if (tags.contains(newTag)) {
            throw new RuntimeException("Learning Object already contains tag");
        }

        tags.add(newTag);
        LearningObject updatedLearningObject = learningObjectDao.createOrUpdate(learningObject);
        solrEngineService.updateIndex();

        return updatedLearningObject;
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
}
