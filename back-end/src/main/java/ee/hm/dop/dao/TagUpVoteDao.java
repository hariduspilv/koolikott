package ee.hm.dop.dao;

import java.security.InvalidParameterException;
import java.util.List;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;

public class TagUpVoteDao extends AbstractDao<TagUpVote> {

    public void setDeleted(TagUpVote tagUpVote) {
        setDeleted(tagUpVote, true);
    }

    private void setDeleted(TagUpVote tagUpVote, boolean deleted) {
        if (tagUpVote.getId() == null) {
            throw new InvalidParameterException("tagUpVote does not exist.");
        }

        tagUpVote.setDeleted(deleted);
        createOrUpdate(tagUpVote);
    }

    public TagUpVote findByTagAndUserAndLearningObject(Tag tag, User user, LearningObject learningObject) {
        TypedQuery<TagUpVote> query = getEntityManager().createQuery(
                "SELECT t FROM TagUpVote t WHERE t.deleted = false " +
                        "and t.learningObject = :learningObject " +
                        "and t.user = :user and t.tag = :tag", entity())
                .setParameter("learningObject", learningObject)
                .setParameter("user", user)
                .setParameter("tag", tag);
        return getSingleResult(query);
    }

    public List<TagUpVote> findByLearningObjectAndTag(LearningObject learningObject, Tag tag) {
        return getEntityManager().createQuery(
                "SELECT t FROM TagUpVote t WHERE t.learningObject = :learningObject AND t.tag = :tag AND t.deleted = false", entity()) //
                .setParameter("learningObject", learningObject) //
                .setParameter("tag", tag) //
                .getResultList();
    }
}
