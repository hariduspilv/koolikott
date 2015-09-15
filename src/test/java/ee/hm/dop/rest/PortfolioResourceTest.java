package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Portfolio;

public class PortfolioResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_PORTFOLIO_URL = "portfolio?id=%s";

    @Test
    public void getPortfolio() {
        Portfolio portfolio = doGet(format(GET_PORTFOLIO_URL, 1), Portfolio.class);

        assertNotNull(portfolio);
        assertEquals(new Long(1), portfolio.getId());
        assertEquals("The new stock market", portfolio.getTitle());
        assertEquals(new Long(2), portfolio.getSubject().getId());
        assertEquals(new DateTime("2000-12-29T08:00:01.000+02:00"), portfolio.getCreated());
        assertEquals(new DateTime("2004-12-29T08:00:01.000+02:00"), portfolio.getUpdated());
        assertEquals(new Long(1005), portfolio.getEducationalContext().getId());
        assertEquals("CONTINUINGEDUCATION", portfolio.getEducationalContext().getName());
    }
}
