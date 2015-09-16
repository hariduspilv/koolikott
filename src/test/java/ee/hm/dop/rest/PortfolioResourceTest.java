package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Portfolio;

public class PortfolioResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_PORTFOLIO_URL = "portfolio?id=%s";
    private static final String GET_BY_CREATOR_URL = "portfolio/getByCreator?username=%s";

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

    @Test
    public void getByCreator() {
        String username = "mati.maasikas-vaarikas";
        List<Portfolio> portfolios = doGet(format(GET_BY_CREATOR_URL, username))
                .readEntity(new GenericType<List<Portfolio>>() {
                });

        assertEquals(2, portfolios.size());
        assertEquals(Long.valueOf(3), portfolios.get(0).getId());
        assertEquals(Long.valueOf(1), portfolios.get(1).getId());
        assertPortfolio1(portfolios.get(1));
    }

    @Test
    public void getByCreatorWithoutUsername() {
        Response response = doGet("portfolio/getByCreator");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Username parameter is mandatory", response.readEntity(String.class));
    }

    @Test
    public void getByCreatorWithBlankUsername() {
        Response response = doGet(format(GET_BY_CREATOR_URL, ""));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Username parameter is mandatory", response.readEntity(String.class));
    }

    @Test
    public void getByCreatorNotExistingUser() {
        String username = "notexisting.user";
        Response response = doGet(format(GET_BY_CREATOR_URL, username));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Invalid request", response.readEntity(String.class));
    }

    @Test
    public void getByCreatorNoMaterials() {
        String username = "voldemar.vapustav";
        List<Portfolio> portfolios = doGet(format(GET_BY_CREATOR_URL, username))
                .readEntity(new GenericType<List<Portfolio>>() {
                });

        assertEquals(0, portfolios.size());
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
    }
}
