package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.common.test.TestLayer;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PortfolioDaoTest extends DatabaseTestBase {

    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private UserDao userDao;

    private int threadsDone;

    @Test
    public void findById() {
        Portfolio portfolio = portfolioDao.findByIdNotDeleted(PORTFOLIO_1);
        assertPortfolio1(portfolio, TestLayer.DAO);
    }

    @Test
    public void findByIdWhenPortfolioDoesNotExist() {
        Portfolio portfolio = portfolioDao.findByIdNotDeleted(100000);
        assertNull(portfolio);
    }

    @Test
    public void findByIdOnlyMandatoryFields() {
        Portfolio portfolio = portfolioDao.findByIdNotDeleted(PORTFOLIO_2);

        assertNotNull(portfolio);
        assertEquals(PORTFOLIO_2, portfolio.getId());
        assertEquals("New ways how to do it", portfolio.getTitle());
        assertEquals(new DateTime("2012-12-29T08:00:01.000+02:00"), portfolio.getAdded());
        assertTrue(portfolio.getTaxons().size() == 0);
        assertEquals(USER_VOLDERMAR2.id, portfolio.getCreator().getId());
        assertEquals(USER_VOLDERMAR2.username, portfolio.getCreator().getUsername());
        assertNull(portfolio.getSummary());
        assertEquals(new Long(14), portfolio.getViews());
        assertTrue(portfolio.getChapters().isEmpty());
        assertTrue(portfolio.getTags().isEmpty());
    }

    @Test
    public void findAllById() {
        List<Long> idList = new ArrayList<>();
        idList.add(PORTFOLIO_1);
        idList.add(PORTFOLIO_2);
        idList.add(PORTFOLIO_3);

        List<LearningObject> result = portfolioDao.findAllById(idList);
        assertEquals(3, result.size());

        for (LearningObject portfolio : result) {
            idList.remove(portfolio.getId());
        }
        assertTrue(idList.isEmpty());
    }

    @Test
    public void findAllByIdNoResult() {
        List<Long> idList = new ArrayList<>();
        idList.add((long) 90123);

        List<LearningObject> result = portfolioDao.findAllById(idList);
        assertEquals(0, result.size());
    }

    @Test
    public void findAllByIdEmptyList() {
        List<LearningObject> result = portfolioDao.findAllById(new ArrayList<>());
        assertEquals(0, result.size());
    }

    @Test
    public void findByCreator() {
        User creator = new User();
        creator.setId(USER_MAASIKAS_VAARIKAS.id);

        List<LearningObject> portfolios = portfolioDao.findByCreator(creator, 0, Integer.MAX_VALUE);
        assertEquals(3, portfolios.size());
        DateTime previous = null;

        for (LearningObject portfolio : portfolios) {
            assertEquals(USER_MAASIKAS_VAARIKAS.username, portfolio.getCreator().getUsername());
            if (portfolio.getId().equals(PORTFOLIO_1)) {
                assertPortfolio1((Portfolio) portfolio, TestLayer.DAO);
            }

            if (previous != null) {
                assertTrue(previous.isAfter(portfolio.getAdded()));
            }

            previous = portfolio.getAdded();
        }
    }

    @Test
    public void increaseViewCount() {
        Portfolio portfolio = portfolioDao.findByIdNotDeleted(PORTFOLIO_2);
        long originalViews = portfolio.getViews();
        assertSame(14L, originalViews);

        portfolio.setViews(++originalViews);
        portfolioDao.incrementViewCount(portfolio);

        Portfolio returnedPortfolio = portfolioDao.findByIdNotDeleted(PORTFOLIO_2);
        assertSame(15L, returnedPortfolio.getViews());

        returnedPortfolio.setViews(14L);
        Portfolio originalPortfolio = portfolioDao.createOrUpdate(returnedPortfolio);
        assertSame(14L, originalPortfolio.getViews());
    }

    @Test
    public void increaseViewCountAtTheSameTime() {
        threadsDone = 0;

        class IncreaseViewCountThread implements Runnable {

            @Override
            public void run() {
                Portfolio portfolio = new Portfolio();
                portfolio.setId(2L);

                for (int i = 0; i < 10; i++) {
                    portfolioDao.incrementViewCount(portfolio);
                }

                threadsDone++;
            }
        }

        int totalThreads = 10;
        for (int i = 0; i < totalThreads; i++) {
            new IncreaseViewCountThread().run();
        }

        while (threadsDone < totalThreads) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        Portfolio newPortfolio = portfolioDao.findByIdNotDeleted(PORTFOLIO_2);
        assertSame(14L, newPortfolio.getViews());

        newPortfolio.setViews(14L);
        portfolioDao.createOrUpdate(newPortfolio);
    }

    @Test
    public void addComment() {
        User user = userDao.findUserByIdCode("37066990099");
        String unique_comment = "UNIQUE" + System.currentTimeMillis();

        Comment comment = new Comment();
        comment.setText(unique_comment);
        comment.setCreator(user);
        comment.setAdded(DateTime.now());

        Portfolio newPortfolio = portfolioDao.findByIdNotDeleted(PORTFOLIO_2);
        newPortfolio.getComments().add(comment);

        portfolioDao.createOrUpdate(newPortfolio);
    }

}
