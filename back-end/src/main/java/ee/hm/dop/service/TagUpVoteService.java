package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.TagUpVoteDAO;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;

public class TagUpVoteService {

    @Inject
    private TagUpVoteDAO tagUpVoteDAO;

    @Inject
    private SearchEngineService searchEngineService;

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
        TagUpVote tagUpVote = tagUpVoteDAO.findById(id);

        if (tagUpVote != null) {
            validateIfUserHasAccessAndThrowExceptionIfNot(tagUpVote, user);
        }

        return tagUpVote;
    }

    public TagUpVote upVote(TagUpVote tagUpVote, User user) {
        validateIfUserHasAccessAndThrowExceptionIfNot(tagUpVote, user);

        tagUpVote.setUser(user);

        TagUpVote returnTagUpVote = tagUpVoteDAO.update(tagUpVote);
        searchEngineService.updateIndex();

        return returnTagUpVote;
    }

    public void delete(TagUpVote tagUpVote, User user) {
        validateIfUserHasAccessAndThrowExceptionIfNot(tagUpVote, user);
        tagUpVoteDAO.setDeleted(tagUpVote);
        searchEngineService.updateIndex();
    }

    public int getUpVoteCountFor(Tag tag, LearningObject learningObject) {
        return tagUpVoteDAO.findByLearningObjectAndTag(learningObject, tag).size();
    }

    public TagUpVote getTagUpVote(Tag tag, LearningObject learningObject, User user) {
        if (!learningObjectService.hasAccess(user, learningObject)) {
            return null;
        }

        return tagUpVoteDAO.findByTagAndUserAndLearningObject(tag, user, learningObject);
    }

    private void validateIfUserHasAccessAndThrowExceptionIfNot(TagUpVote tagUpVote, User user) {
        if ((tagUpVote.getId() != null && user.getId() != tagUpVote.getUser().getId())
                || !learningObjectService.hasAccess(user, tagUpVote.getLearningObject())) {
            throw new RuntimeException("Access denied");
        }
    }
}
