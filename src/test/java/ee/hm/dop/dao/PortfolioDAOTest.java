package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

public class PortfolioDAOTest extends DatabaseTestBase {

    @Inject
    private PortfolioDAO portfolioDAO;

    private int threadsDone;

    @Test
    public void findById() {
        Portfolio portfolio = portfolioDAO.findById(1);

        assertPortfolio1(portfolio);
    }

    @Test
    public void findByIdWhenPortfolioDoesNotExist() {
        Portfolio portfolio = portfolioDAO.findById(100000);
        assertNull(portfolio);
    }

    @Test
    public void findByIdOnlyMandatoryFields() {
        Long id = new Long(2);
        Portfolio portfolio = portfolioDAO.findById(id);

        assertNotNull(portfolio);
        assertEquals(id, portfolio.getId());
        assertEquals("New ways how to do it", portfolio.getTitle());
        assertNull(portfolio.getSubject());
        assertEquals(new DateTime("2012-12-29T08:00:01.000+02:00"), portfolio.getCreated());
        assertNull(portfolio.getEducationalContext());
        assertEquals(new Long(4), portfolio.getCreator().getId());
        assertEquals("voldemar.vapustav2", portfolio.getCreator().getUsername());
        assertNull(portfolio.getSummary());
        assertEquals(new Long(14), portfolio.getViews());
        assertTrue(portfolio.getChapters().isEmpty());
        assertTrue(portfolio.getTags().isEmpty());
    }

    @Test
    public void findByCreator() {
        User creator = new User();
        creator.setId(6L);

        List<Portfolio> portfolios = portfolioDAO.findByCreator(creator);
        assertEquals(2, portfolios.size());
        DateTime previous = null;

        for (Portfolio portfolio : portfolios) {
            assertEquals("mati.maasikas-vaarikas", portfolio.getCreator().getUsername());
            if (portfolio.getId().equals(Long.valueOf(1))) {
                assertPortfolio1(portfolio);
            }

            if (previous != null) {
                assertTrue(previous.isAfter(portfolio.getCreated()));
            }

            previous = portfolio.getCreated();
        }
    }

    private void assertPortfolio1(Portfolio portfolio) {
        assertNotNull(portfolio);
        assertEquals(Long.valueOf(1), portfolio.getId());
        assertEquals("The new stock market", portfolio.getTitle());
        assertEquals("Mathematics", portfolio.getSubject().getName());
        assertEquals(new DateTime("2000-12-29T08:00:01.000+02:00"), portfolio.getCreated());
        assertEquals(new DateTime("2004-12-29T08:00:01.000+02:00"), portfolio.getUpdated());
        assertEquals("CONTINUINGEDUCATION", portfolio.getEducationalContext().getName());
        assertEquals(new Long(6), portfolio.getCreator().getId());
        assertEquals("mati.maasikas-vaarikas", portfolio.getCreator().getUsername());
        assertEquals("The changes after 2008.", portfolio.getSummary());
        assertEquals(new Long(95455215), portfolio.getViews());
        assertEquals(5, portfolio.getTags().size());

        List<Chapter> chapters = portfolio.getChapters();
        assertEquals(3, chapters.size());
        Chapter chapter = chapters.get(0);
        assertEquals(new Long(1), chapter.getId());
        assertEquals("The crisis", chapter.getTitle());
        assertNull(chapter.getText());
        assertEquals(new Long(4), chapter.getSubchapter().getId());
        assertEquals("Subprime", chapter.getSubchapter().getTitle());

        chapter = chapters.get(1);
        assertEquals(new Long(3), chapter.getId());
        assertEquals("Chapter 2", chapter.getTitle());
        assertEquals("Paragraph 1\n\nParagraph 2\n\nParagraph 3\n\nParagraph 4", chapter.getText());
        assertNull(chapter.getSubchapter());

        chapter = chapters.get(2);
        assertEquals(new Long(2), chapter.getId());
        assertEquals("Chapter 3", chapter.getTitle());
        assertEquals("This is some text that explains what is the Chapter 3 about.\nIt can have many lines\n\n\n"
                + "And can also have    spaces   betwenn    the words on it", chapter.getText());
        assertNull(chapter.getSubchapter());
    }

    @Test
    public void findPictureByPortfolio() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId((long) 1);
        byte[] picture = portfolioDAO.findPictureByPortfolio(portfolio);
        assertNotNull(picture);
    }

    @Test
    public void findPictureByPortfolioNoPicture() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId((long) 2);
        byte[] picture = portfolioDAO.findPictureByPortfolio(portfolio);
        assertNull(picture);
    }

    @Test
    public void findPictureByPortfoNullId() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(null);
        byte[] picture = portfolioDAO.findPictureByPortfolio(portfolio);
        assertNull(picture);
    }

    @Test
    public void getHasPictureTrue() {
        Portfolio portfolio = portfolioDAO.findById(1);
        assertTrue(portfolio.getHasPicture());
    }

    @Test
    public void getHasPictureNoPicture() {
        Portfolio portfolio = portfolioDAO.findById(2);
        assertFalse(portfolio.getHasPicture());
    }

    @Test
    public void increaseViewCount() {
        Portfolio portfolio = portfolioDAO.findById(2);
        long originalViews = portfolio.getViews();
        assertSame(14L, originalViews);

        portfolio.setViews(++originalViews);
        portfolioDAO.incrementViewCount(portfolio);

        Portfolio returnedPortfolio = portfolioDAO.findById(2);
        assertSame(15L, returnedPortfolio.getViews());

        returnedPortfolio.setViews(14L);
        Portfolio originalPortfolio = portfolioDAO.update(returnedPortfolio);
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
                    portfolioDAO.incrementViewCount(portfolio);
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

        Portfolio newPortfolio = portfolioDAO.findById(2L);
        assertSame(114L, newPortfolio.getViews());

        newPortfolio.setViews(14L);
        portfolioDAO.update(newPortfolio);
    }
}
