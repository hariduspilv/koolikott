package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;
import org.junit.Test;

public class TagUpVoteDaoTest extends DatabaseTestBase {

    @Inject
    private TagUpVoteDao tagUpVoteDao;
    @Inject
    private UserDao userDao;
    @Inject
    private TagDao tagDao;
    @Inject
    private MaterialDao materialDao;
    @Inject
    private PortfolioDao portfolioDao;

    @Test
    public void addUpVote() {
        User user = userDao.findUserByIdCode(ResourceIntegrationTestBase.USER_MATI);
        Tag tag = tagDao.findByName("matemaatika");
        Material material = materialDao.findById(TestConstants.MATERIAL_1);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setUser(user);
        tagUpVote.setLearningObject(material);

        TagUpVote returnedUpVote = tagUpVoteDao.createOrUpdate(tagUpVote);

        assertNotNull(returnedUpVote.getId());

        tagUpVoteDao.setDeleted(returnedUpVote);
    }

    @Test
    public void getUpVoteForMaterial() {
        User user = userDao.findUserByIdCode(ResourceIntegrationTestBase.USER_MATI);
        Tag tag = tagDao.findByName("matemaatika");
        Material material = materialDao.findById(TestConstants.MATERIAL_1);

        TagUpVote tagUpVote = tagUpVoteDao.findByTagAndUserAndLearningObject(tag, user, material);
        assertNotNull(tagUpVote);
        assertNotNull(tagUpVote.getId());
    }

    @Test
    public void getUpVoteForPortfolio() {
        User user = userDao.findUserByIdCode(ResourceIntegrationTestBase.USER_MATI);
        Tag tag = tagDao.findByName("matemaatika");
        Portfolio portfolio = portfolioDao.findById(TestConstants.PORTFOLIO_1);

        TagUpVote tagUpVote = tagUpVoteDao.findByTagAndUserAndLearningObject(tag, user, portfolio);
        assertNotNull(tagUpVote);
        assertNotNull(tagUpVote.getId());
    }

    @Test
    public void getMaterialTagUpVotes() {
        Tag tag = tagDao.findByName("matemaatika");
        Material material = materialDao.findById(TestConstants.MATERIAL_1);

        List<TagUpVote> tagUpVotes = tagUpVoteDao.findByLearningObjectAndTag(material, tag);
        assertNotNull(tagUpVotes);
        assertEquals(1, tagUpVotes.size());
    }

    @Test
    public void getPortfolioTagUpVotes() {
        Tag tag = tagDao.findByName("matemaatika");
        Portfolio portfolio = portfolioDao.findById(TestConstants.PORTFOLIO_1);

        List<TagUpVote> tagUpVotes = tagUpVoteDao.findByLearningObjectAndTag(portfolio, tag);
        assertNotNull(tagUpVotes);
        assertEquals(1, tagUpVotes.size());
    }
}
