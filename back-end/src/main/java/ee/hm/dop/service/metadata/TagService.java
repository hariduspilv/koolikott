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

    public LearningObject addRegularTag(Long learningObjectId, Tag tag, User loggedInUser) {
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);
        ValidatorUtil.mustHaveEntity(learningObject);

        return addTag(learningObject, tag, loggedInUser);
    }

    public TagDTO addSystemTag(Long learningObjectId, Tag tag, User user) {
        LearningObject learningObject = learningObjectDao.findById(learningObjectId);
        ValidatorUtil.mustHaveEntity(learningObject);

        LearningObject newLearningObject = addTag(learningObject, tag, user);
        //todo why does system tag return tag dto
        return tagConverter.addChangeReturnTagDto(tag.getName(), newLearningObject, user);
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

}
