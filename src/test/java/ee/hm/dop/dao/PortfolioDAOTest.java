package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        assertNull(portfolio.getUpdated());
        assertNull(portfolio.getEducationalContext());
        assertEquals(new Long(4), portfolio.getCreator().getId());
        assertEquals("voldemar.vapustav2", portfolio.getCreator().getUsername());
        assertNull(portfolio.getSummary());
        assertEquals(new Long(14), portfolio.getViews());
    }

    @Test
    public void findByCreator() {
        User creator = new User();
        creator.setId(6L);

        List<Portfolio> portfolios = portfolioDAO.findByCreator(creator);
        assertEquals(2, portfolios.size());
        DateTime previous = null;

        for(Portfolio portfolio : portfolios) {
            assertEquals("mati.maasikas-vaarikas", portfolio.getCreator().getUsername());
            if(portfolio.getId().equals(Long.valueOf(1))) {
                assertPortfolio1(portfolio);
            }

            if(previous != null){
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
    }
}
