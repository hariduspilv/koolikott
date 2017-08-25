package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.TagUpVoteDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;
import ee.hm.dop.service.solr.SolrEngineService;

public class TagUpVoteService {

    @Inject
    private TagUpVoteDao tagUpVoteDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private LearningObjectService learningObjectService;

    /**
     * 
     * @param id
     *            the TagUpVote id
     * @param user
     *            the user who wants access to the TagUpVote
     * @return the tagUpVote if user has access
     */
    public TagUpVote get(long id, User user) {
        TagUpVote tagUpVote = tagUpVoteDao.findById(id);

        if (tagUpVote != null) {
            validateIfUserHasAccessAndThrowExceptionIfNot(tagUpVote, user);
        }

        return tagUpVote;
    }

    public TagUpVote upVote(TagUpVote tagUpVote, User user) {
        validateIfUserHasAccessAndThrowExceptionIfNot(tagUpVote, user);

        tagUpVote.setUser(user);

        TagUpVote returnTagUpVote = tagUpVoteDao.createOrUpdate(tagUpVote);
        solrEngineService.updateIndex();

        return returnTagUpVote;
    }

    public void delete(TagUpVote tagUpVote, User user) {
        validateIfUserHasAccessAndThrowExceptionIfNot(tagUpVote, user);
        tagUpVoteDao.setDeleted(tagUpVote);
        solrEngineService.updateIndex();
    }

    public int getUpVoteCountFor(Tag tag, LearningObject learningObject) {
        return tagUpVoteDao.findByLearningObjectAndTag(learningObject, tag).size();
    }

    public TagUpVote getTagUpVote(Tag tag, LearningObject learningObject, User user) {
        if (!learningObjectService.hasPermissionsToAccess(user, learningObject)) {
            return null;
        }

        return tagUpVoteDao.findByTagAndUserAndLearningObject(tag, user, learningObject);
    }

    private void validateIfUserHasAccessAndThrowExceptionIfNot(TagUpVote tagUpVote, User user) {
        if ((tagUpVote.getId() != null && user.getId() != tagUpVote.getUser().getId())
                || !learningObjectService.hasPermissionsToAccess(user, tagUpVote.getLearningObject())) {
            throw new RuntimeException("Access denied");
        }
    }
}
