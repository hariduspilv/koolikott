package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;
import org.junit.Test;

public class TagUpVoteDAOTest extends DatabaseTestBase {

    @Inject
    private TagUpVoteDAO tagUpVoteDAO;

    @Inject
    private UserDao userDao;

    @Inject
    private TagDao tagDao;

    @Inject
    private MaterialDAO materialDAO;

    @Inject
    private PortfolioDAO portfolioDAO;

    @Test
    public void addUpVote() {
        User user = userDao.findUserByIdCode("39011220011");
        Tag tag = tagDao.findByName("matemaatika");
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
        User user = userDao.findUserByIdCode("39011220011");
        Tag tag = tagDao.findByName("matemaatika");
        Material material = materialDAO.findById(1l);

        TagUpVote tagUpVote = tagUpVoteDAO.findByTagAndUserAndLearningObject(tag, user, material);
        assertNotNull(tagUpVote);
        assertNotNull(tagUpVote.getId());
    }

    @Test
    public void getUpVoteForPortfolio() {
        User user = userDao.findUserByIdCode("39011220011");
        Tag tag = tagDao.findByName("matemaatika");
        Portfolio portfolio = portfolioDAO.findByIdFromAll(101l);

        TagUpVote tagUpVote = tagUpVoteDAO.findByTagAndUserAndLearningObject(tag, user, portfolio);
        assertNotNull(tagUpVote);
        assertNotNull(tagUpVote.getId());
    }

    @Test
    public void getMaterialTagUpVotes() {
        Tag tag = tagDao.findByName("matemaatika");
        Material material = materialDAO.findById(1l);

        List<TagUpVote> tagUpVotes = tagUpVoteDAO.findByLearningObjectAndTag(material, tag);
        assertNotNull(tagUpVotes);
        assertEquals(1, tagUpVotes.size());
    }

    @Test
    public void getPortfolioTagUpVotes() {
        Tag tag = tagDao.findByName("matemaatika");
        Portfolio portfolio = portfolioDAO.findByIdFromAll(101l);

        List<TagUpVote> tagUpVotes = tagUpVoteDAO.findByLearningObjectAndTag(portfolio, tag);
        assertNotNull(tagUpVotes);
        assertEquals(1, tagUpVotes.size());
    }
}
