package ee.hm.dop.dao;

import java.security.InvalidParameterException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.TagUpVote;

/**
 * Created by mart on 29.01.16.
 */
public class TagUpVoteDAO  {

    @Inject
    private EntityManager entityManager;

    public TagUpVote update(TagUpVote tagUpVote) {

        TagUpVote merged = entityManager.merge(tagUpVote);
        entityManager.persist(merged);
        return merged;
    }

    public void setDeleted(TagUpVote tagUpVote, boolean deleted) {
        if (tagUpVote.getId() == null) {
            throw new InvalidParameterException("tagUpVote does not exist.");
        }

        tagUpVote.setDeleted(deleted);
        update(tagUpVote);
    }

    public List<TagUpVote> getNotDeletedUpVotes() {
        TypedQuery<TagUpVote> query = entityManager.createQuery("SELECT t FROM TagUpVote t WHERE t.deleted = false",
                TagUpVote.class);
        return query.getResultList();
    }
}
