package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Portfolio;

public class PortfolioResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_PORTFOLIO_URL = "portfolio?id=%s";
    private static final String GET_BY_CREATOR_URL = "portfolio/getByCreator?username=%s";
    private static final String GET_PORTFOLIO_PICTURE_URL = "portfolio/getPicture?portfolioId=%s";
    private static final String PORTFOLIO_INCREASE_VIEW_COUNT_URL = "portfolio/increaseViewCount";

    @Test
    public void getPortfolio() {
        Portfolio portfolio = getPortfolio(1);
        assertPortfolio1(portfolio);
    }

    @Test
    public void getByCreator() {
        String username = "mati.maasikas-vaarikas";
        List<Portfolio> portfolios = doGet(format(GET_BY_CREATOR_URL, username)).readEntity(
                new GenericType<List<Portfolio>>() {
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
        List<Portfolio> portfolios = doGet(format(GET_BY_CREATOR_URL, username)).readEntity(
                new GenericType<List<Portfolio>>() {
                });

        assertEquals(0, portfolios.size());
    }

    @Test
    public void getPortfolioPicture() {
        long portfolioId = 1;
        Response response = doGet(format(GET_PORTFOLIO_PICTURE_URL, portfolioId), MediaType.WILDCARD_TYPE);
        byte[] picture = response.readEntity(new GenericType<byte[]>() {
        });
        assertNotNull(picture);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPortfolioPictureNull() {
        long portfolioId = 2;
        Response response = doGet(format(GET_PORTFOLIO_PICTURE_URL, portfolioId), MediaType.WILDCARD_TYPE);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPortfolioPictureIdNull() {
        Response response = doGet(format(GET_PORTFOLIO_PICTURE_URL, "null"), MediaType.WILDCARD_TYPE);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void increaseViewCount() {
        long id = 3;
        Portfolio portfolioBefore = getPortfolio(id);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);

        doPost(PORTFOLIO_INCREASE_VIEW_COUNT_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));

        Portfolio portfolioAfter = getPortfolio(id);

        assertEquals(Long.valueOf(portfolioBefore.getViews() + 1), portfolioAfter.getViews());
    }

    @Test
    public void increaseViewCountNoPortfolio() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(99999L);

        Response response = doPost(PORTFOLIO_INCREASE_VIEW_COUNT_URL,
                Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(500, response.getStatus());
    }

    private Portfolio getPortfolio(long id) {
        return doGet(format(GET_PORTFOLIO_URL, id), Portfolio.class);
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
        assertEquals(new Long(1), chapter.getMaterial().getId());
        assertEquals(2, chapter.getSubchapters().size());
        Chapter subchapter1 = chapter.getSubchapters().get(0);
        assertEquals(new Long(4), subchapter1.getId());
        assertEquals("Subprime", subchapter1.getTitle());
        assertNull(subchapter1.getText());
        assertEquals(new Long(5), subchapter1.getMaterial().getId());
        Chapter subchapter2 = chapter.getSubchapters().get(1);
        assertEquals(new Long(5), subchapter2.getId());
        assertEquals("The big crash", subchapter2.getTitle());
        assertEquals("Bla bla bla\nBla bla bla bla bla bla bla", subchapter2.getText());
        assertEquals(new Long(3), subchapter2.getMaterial().getId());

        chapter = chapters.get(1);
        assertEquals(new Long(3), chapter.getId());
        assertEquals("Chapter 2", chapter.getTitle());
        assertEquals("Paragraph 1\n\nParagraph 2\n\nParagraph 3\n\nParagraph 4", chapter.getText());
        assertNull(chapter.getMaterial());
        assertEquals(0, chapter.getSubchapters().size());

        chapter = chapters.get(2);
        assertEquals(new Long(2), chapter.getId());
        assertEquals("Chapter 3", chapter.getTitle());
        assertEquals("This is some text that explains what is the Chapter 3 about.\nIt can have many lines\n\n\n"
                + "And can also have    spaces   betwenn    the words on it", chapter.getText());
        assertNull(chapter.getMaterial());
        assertEquals(0, chapter.getSubchapters().size());
    }
}
