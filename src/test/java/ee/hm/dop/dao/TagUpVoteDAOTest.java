package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
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

    @Inject
    private PortfolioDAO portfolioDAO;

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

        tagUpVoteDAO.setDeleted(returnedUpVote);

        tagUpVotes = tagUpVoteDAO.getNotDeletedUpVotes();
        assertEquals(2, tagUpVotes.size());
    }

    @Test
    public void getUpVoteForMaterial() {
        User user = userDAO.findUserByIdCode("39011220011");
        Tag tag = tagDAO.findTagByName("matemaatika");
        Material material = materialDAO.findById(1l);

        TagUpVote tagUpVote = tagUpVoteDAO.getTagUpVote(tag, user, material);
        assertNotNull(tagUpVote);
        assertNotNull(tagUpVote.getId());
    }

    @Test
    public void getUpVoteForPortfolio() {
        User user = userDAO.findUserByIdCode("39011220011");
        Tag tag = tagDAO.findTagByName("matemaatika");
        Portfolio portfolio = portfolioDAO.findByIdFromAll(1l);

        TagUpVote tagUpVote = tagUpVoteDAO.getTagUpVote(tag, user, portfolio);
        assertNotNull(tagUpVote);
        assertNotNull(tagUpVote.getId());
    }
}
