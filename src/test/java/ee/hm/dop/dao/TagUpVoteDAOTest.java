package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;

/**
 * Created by mart on 1.02.16.
 */
public class TagUpVoteDAOTest extends DatabaseTestBase {

    @Inject
    private TagUpVoteDAO tagUpVoteDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private TagDAO tagDAO;

    @Inject
    private MaterialDAO materialDAO;

    @Test
    public void getAllNotDeleted() {
        List<TagUpVote> tagUpVotes = tagUpVoteDAO.getNotDeletedUpVotes();

        assertEquals(2, tagUpVotes.size());
    }

    @Test
    public void addUpVote() {
        User user = userDAO.findUserByIdCode("39011220011");
        Tag tag = tagDAO.findTagByName("matemaatika");
        Material material = materialDAO.findById(1l);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setUser(user);
        tagUpVote.setMaterial(material);

        List<TagUpVote> tagUpVotes = tagUpVoteDAO.getNotDeletedUpVotes();
        assertEquals(2, tagUpVotes.size());

        TagUpVote returnedUpVote = tagUpVoteDAO.update(tagUpVote);

        tagUpVotes = tagUpVoteDAO.getNotDeletedUpVotes();
        assertEquals(3, tagUpVotes.size());

        tagUpVoteDAO.setDeleted(returnedUpVote, true);

        tagUpVotes = tagUpVoteDAO.getNotDeletedUpVotes();
        assertEquals(2, tagUpVotes.size());
    }
}
