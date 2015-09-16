package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

public class PortfolioDAOTest extends DatabaseTestBase {

    @Inject
    private PortfolioDAO portfolioDAO;

    @Test
    public void findById() {
        Portfolio portfolio = portfolioDAO.findById(1);

        assertNotNull(portfolio);
        assertEquals(new Long(1), portfolio.getId());
        assertEquals("The new stock market", portfolio.getTitle());
        assertEquals(new Long(2), portfolio.getSubject().getId());
        assertEquals(new DateTime("2000-12-29T08:00:01.000+02:00"), portfolio.getCreated());
        assertEquals(new DateTime("2004-12-29T08:00:01.000+02:00"), portfolio.getUpdated());
        assertEquals(new Long(1005), portfolio.getEducationalContext().getId());
        assertEquals("CONTINUINGEDUCATION", portfolio.getEducationalContext().getName());
        assertEquals(new Long(6), portfolio.getCreator().getId());
        assertEquals("mati.maasikas-vaarikas", portfolio.getCreator().getUsername());
        assertEquals("The changes after 2008.", portfolio.getSummary());
        assertEquals(new Long(95455215), portfolio.getViews());
    }

    @Test
    public void findByIdWhenPortfolioDoesNotExist() {
        Portfolio portfolio = portfolioDAO.findById(100000);
        assertNull(portfolio);
    }

    @Test
    public void findByIdOnlyMandatoryFields() {
        Long id = new Long(1);
        Portfolio portfolio = portfolioDAO.findById(id);

        assertPortfolio1(portfolio);
    }

    @Test
    public void findByCreator() {
        User creator = new User();
        creator.setId(6L);

        List<Portfolio> portfolios = portfolioDAO.findByCreator(creator);

        assertEquals(2, portfolios.size());
        assertEquals(Long.valueOf(3), portfolios.get(0).getId());
        assertEquals(Long.valueOf(1), portfolios.get(1).getId());
        assertPortfolio1(portfolios.get(1));
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
    }
}
