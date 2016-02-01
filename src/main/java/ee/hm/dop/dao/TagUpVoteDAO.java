package ee.hm.dop.dao;

import java.security.InvalidParameterException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;

/**
 * Created by mart on 29.01.16.
 */
public class TagUpVoteDAO {

    @Inject
    private EntityManager entityManager;

    public TagUpVote update(TagUpVote tagUpVote) {

        TagUpVote merged = entityManager.merge(tagUpVote);
        entityManager.persist(merged);
        return merged;
    }

    public void setDeleted(TagUpVote tagUpVote) {
        setDeleted(tagUpVote, true);
    }

    public void setNotDeleted(TagUpVote tagUpVote) {
        setDeleted(tagUpVote, false);
    }

    private void setDeleted(TagUpVote tagUpVote, boolean deleted) {
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

    public TagUpVote getTagUpVote(Tag tag, User user, Material material) {
        TypedQuery<TagUpVote> query;
        if (material != null) {
            query = entityManager
                    .createQuery("SELECT t FROM TagUpVote t WHERE t.deleted = false and t.material = :material and t.user = :user and t.tag = :tag",
                            TagUpVote.class).setParameter("material", material).setParameter("user", user).setParameter("tag", tag);
        } else {
            throw new InvalidParameterException("Missing material");

        }

        return query.getSingleResult();
    }

    public TagUpVote getTagUpVote(Tag tag, User user, Portfolio portfolio) {
        TypedQuery<TagUpVote> query;
        if (portfolio != null) {
            query = entityManager
                    .createQuery("SELECT t FROM TagUpVote t WHERE t.deleted = false and t.portfolio = :portfolio and t.user = :user and t.tag = :tag",
                            TagUpVote.class).setParameter("portfolio", portfolio).setParameter("user", user).setParameter("tag", tag);
        } else {
            throw new InvalidParameterException("Missing portfolio");

        }

        return query.getSingleResult();
    }
}
