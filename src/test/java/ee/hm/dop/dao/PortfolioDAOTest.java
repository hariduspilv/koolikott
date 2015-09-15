package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Portfolio;

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
    }

    @Test
    public void findByIdWhenPortfolioDoesNotExist() {
        Portfolio portfolio = portfolioDAO.findById(100000);
        assertNull(portfolio);
    }

    @Test
    public void findByIdNullSubjectAndUpdated() {
        Long id = new Long(2);
        Portfolio portfolio = portfolioDAO.findById(id);

        assertNotNull(portfolio);
        assertEquals(id, portfolio.getId());
        assertEquals("New ways how to do it", portfolio.getTitle());
        assertNull(portfolio.getSubject());
        assertEquals(new DateTime("2012-12-29T08:00:01.000+02:00"), portfolio.getCreated());
        assertNull(portfolio.getUpdated());
    }
}
