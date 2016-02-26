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
    public void addUpVote() {
        User user = userDAO.findUserByIdCode("39011220011");
        Tag tag = tagDAO.findTagByName("matemaatika");
        Material material = materialDAO.findById(1l);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setUser(user);
        tagUpVote.setLearningObject(material);

        TagUpVote returnedUpVote = tagUpVoteDAO.update(tagUpVote);

        assertNotNull(returnedUpVote.getId());

        tagUpVoteDAO.setDeleted(returnedUpVote);
    }

    @Test
    public void getUpVoteForMaterial() {
        User user = userDAO.findUserByIdCode("39011220011");
        Tag tag = tagDAO.findTagByName("matemaatika");
        Material material = materialDAO.findById(1l);

        TagUpVote tagUpVote = tagUpVoteDAO.findByTagAndUserAndLearningObject(tag, user, material);
        assertNotNull(tagUpVote);
        assertNotNull(tagUpVote.getId());
    }

    @Test
    public void getUpVoteForPortfolio() {
        User user = userDAO.findUserByIdCode("39011220011");
        Tag tag = tagDAO.findTagByName("matemaatika");
        Portfolio portfolio = portfolioDAO.findByIdFromAll(101l);

        TagUpVote tagUpVote = tagUpVoteDAO.findByTagAndUserAndLearningObject(tag, user, portfolio);
        assertNotNull(tagUpVote);
        assertNotNull(tagUpVote.getId());
    }

    @Test
    public void getMaterialTagUpVotes() {
        Tag tag = tagDAO.findTagByName("matemaatika");
        Material material = materialDAO.findById(1l);

        List<TagUpVote> tagUpVotes = tagUpVoteDAO.findByLearningObjectAndTag(material, tag);
        assertNotNull(tagUpVotes);
        assertEquals(1, tagUpVotes.size());
    }

    @Test
    public void getPortfolioTagUpVotes() {
        Tag tag = tagDAO.findTagByName("matemaatika");
        Portfolio portfolio = portfolioDAO.findByIdFromAll(101l);

        List<TagUpVote> tagUpVotes = tagUpVoteDAO.findByLearningObjectAndTag(portfolio, tag);
        assertNotNull(tagUpVotes);
        assertEquals(1, tagUpVotes.size());
    }
}
