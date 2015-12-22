package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.Visibility;

public class PortfolioResourceTest extends ResourceIntegrationTestBase {

    private static final String CREATE_PORTFOLIO_URL = "portfolio/create";
    private static final String UPDATE_PORTFOLIO_URL = "portfolio/update";
    private static final String GET_PORTFOLIO_URL = "portfolio?id=%s";
    private static final String GET_BY_CREATOR_URL = "portfolio/getByCreator?username=%s";
    private static final String GET_PORTFOLIO_PICTURE_URL = "portfolio/getPicture?portfolioId=%s";
    private static final String PORTFOLIO_INCREASE_VIEW_COUNT_URL = "portfolio/increaseViewCount";
    private static final String PORTFOLIO_COPY_URL = "portfolio/copy";
    private static final String DELETE_PORTFOLIO_URL = "portfolio/delete";

    @Test
    public void getPortfolio() {
        Portfolio portfolio = getPortfolio(1);
        assertPortfolio1(portfolio);
    }

    @Test
    public void getNotExistingPortfolio() {
        Response response = doGet(format(GET_PORTFOLIO_URL, 2000));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPrivatePortfolioAsCreator() {
        login("38011550077");
        Long id = 7L;

        Portfolio portfolio = getPortfolio(id);

        assertEquals(id, portfolio.getId());
        assertEquals("This portfolio is private. ", portfolio.getTitle());
    }

    @Test
    public void getPrivatePortfolioAsNotCreator() {
        login("15066990099");
        Long id = 7L;

        Response response = doGet(format(GET_PORTFOLIO_URL, id));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPrivatePortfolioAsAdmin() {
        login("89898989898");
        Long id = 7L;

        Portfolio portfolio = getPortfolio(id);

        assertEquals(id, portfolio.getId());
        assertEquals("This portfolio is private. ", portfolio.getTitle());
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
    public void getByCreatorWhenSomeArePrivateOrNotListed() {
        String username = "my.testuser";
        List<Portfolio> portfolios = doGet(format(GET_BY_CREATOR_URL, username))
                .readEntity(new GenericType<List<Portfolio>>() {
                });

        assertEquals(1, portfolios.size());
        assertEquals(Long.valueOf(9), portfolios.get(0).getId());
    }

    @Test
    public void getByCreatorWhenSomeArePrivateOrNotListedAsCreator() {
        login("78912378912");

        String username = "my.testuser";
        List<Portfolio> portfolios = doGet(format(GET_BY_CREATOR_URL, username))
                .readEntity(new GenericType<List<Portfolio>>() {
                });

        assertEquals(3, portfolios.size());
        List<Long> expectedIds = Arrays.asList(9L, 10L, 11L);
        List<Long> actualIds = portfolios.stream().map(p -> p.getId()).collect(Collectors.toList());
        assertTrue(actualIds.containsAll(expectedIds));
    }

    @Test
    public void getByCreatorWhenSomeArePrivateOrNotListedAsAdmin() {
        login("89898989898");

        String username = "my.testuser";
        List<Portfolio> portfolios = doGet(format(GET_BY_CREATOR_URL, username))
                .readEntity(new GenericType<List<Portfolio>>() {
                });

        assertEquals(3, portfolios.size());
        List<Long> expectedIds = Arrays.asList(9L, 10L, 11L);
        List<Long> actualIds = portfolios.stream().map(p -> p.getId()).collect(Collectors.toList());
        assertTrue(actualIds.containsAll(expectedIds));
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
    public void getPortfolioPictureWhenPortfolioIsPrivate() {
        long portfolioId = 7;
        Response response = doGet(format(GET_PORTFOLIO_PICTURE_URL, portfolioId), MediaType.WILDCARD_TYPE);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPortfolioPictureWhenPortfolioIsPrivateAsCreator() {
        login("38011550077");
        long portfolioId = 7;
        Response response = doGet(format(GET_PORTFOLIO_PICTURE_URL, portfolioId), MediaType.WILDCARD_TYPE);
        byte[] picture = response.readEntity(new GenericType<byte[]>() {
        });
        assertNotNull(picture);
    }

    @Test
    public void getPortfolioPictureWhenPortfolioIsPrivateAsNotCreator() {
        login("39011220011");
        long portfolioId = 7;
        Response response = doGet(format(GET_PORTFOLIO_PICTURE_URL, portfolioId), MediaType.WILDCARD_TYPE);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPortfolioPictureWhenPortfolioIsPrivateAsAdmin() {
        login("89898989898");
        long portfolioId = 7;
        Response response = doGet(format(GET_PORTFOLIO_PICTURE_URL, portfolioId), MediaType.WILDCARD_TYPE);
        byte[] picture = response.readEntity(new GenericType<byte[]>() {
        });
        assertNotNull(picture);
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

    @Test
    public void create() {
        login("39011220011");
        Long id = 1L;

        Portfolio portfolio = new Portfolio();
        portfolio.setTitle("Tere");

        Portfolio createdPortfolio = doPost(CREATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertNotNull(createdPortfolio);
        assertNotNull(createdPortfolio.getId());
        assertEquals(id, createdPortfolio.getOriginalCreator().getId());
        assertEquals(id, createdPortfolio.getCreator().getId());
    }

    @Test
    public void updateChanginMetadataNoChapters() {
        login("39011220011");

        Portfolio portfolio = getPortfolio(5);
        String originalTitle = portfolio.getTitle();
        portfolio.setTitle("New mega nice title that I come with Yesterday night!");

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(originalTitle.equals(updatedPortfolio.getTitle()));
        assertEquals("New mega nice title that I come with Yesterday night!", updatedPortfolio.getTitle());

    }

    @Test
    public void updateSomeoneElsesPortfolio() {
        login("38011550077");

        Portfolio portfolio = getPortfolio(5);
        portfolio.setTitle("This is not my portfolio.");

        // Set creator to the current logged in user
        User creator = new User();
        creator.setId(2L);
        portfolio.setCreator(creator);

        Response response = doPost(UPDATE_PORTFOLIO_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateCreatingChapter() {
        login("39011220011");

        List<Chapter> chapters = new ArrayList<>();

        Chapter newChapter = new Chapter();
        newChapter.setTitle("New chapter 1");
        chapters.add(newChapter);

        Portfolio portfolio = getPortfolio(5);
        portfolio.setChapters(chapters);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);
        assertFalse(updatedPortfolio.getChapters().isEmpty());

    }

    @Test
    public void updateCreatingChapterWithSubchapterNoMaterials() {
        login("39011220011");

        List<Chapter> chapters = new ArrayList<>();
        List<Chapter> subchapters = new ArrayList<>();

        Chapter newChapter = new Chapter();
        newChapter.setTitle("New chapter 1");

        Chapter subChapter = new Chapter();
        subChapter.setTitle("New subchapter");
        subchapters.add(subChapter);
        newChapter.setSubchapters(subchapters);

        chapters.add(newChapter);

        Portfolio portfolio = getPortfolio(5);
        portfolio.setChapters(chapters);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(updatedPortfolio.getChapters().isEmpty());
        assertFalse(updatedPortfolio.getChapters().get(0).getSubchapters().isEmpty());
    }

    @Test
    public void updateCreatingChapterWithExistingChapter() {
        login("39011220011");

        List<Chapter> subchapters = new ArrayList<>();

        Chapter newChapter = new Chapter();
        newChapter.setTitle("New chapter 1");

        Chapter subChapter = new Chapter();
        subChapter.setTitle("New cool subchapter");
        subchapters.add(subChapter);
        newChapter.setSubchapters(subchapters);

        Portfolio portfolio = getPortfolio(5);
        portfolio.getChapters().add(newChapter);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(updatedPortfolio.getChapters().isEmpty());
        Chapter verify = updatedPortfolio.getChapters().get(updatedPortfolio.getChapters().size() - 1).getSubchapters()
                .get(0);
        assertEquals(verify.getTitle(), "New cool subchapter");

    }

    @Test
    public void updateChangingVisibility() {
        login("38011550077");

        Portfolio portfolio = getPortfolio(6);
        portfolio.setVisibility(Visibility.NOT_LISTED);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertEquals(Visibility.NOT_LISTED, updatedPortfolio.getVisibility());
    }

    @Test
    public void copyPortfolio() {
        login("38011550077");

        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);

        Portfolio copiedPortfolio = doPost(PORTFOLIO_COPY_URL, portfolio, Portfolio.class);

        assertNotNull(copiedPortfolio);
        assertEquals(Long.valueOf(2), copiedPortfolio.getCreator().getId());
        assertEquals(Long.valueOf(6), copiedPortfolio.getOriginalCreator().getId());
    }

    @Test
    public void copyPrivatePortfolioNotLoggedIn() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(7L);

        Response response = doPost(PORTFOLIO_COPY_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void copyPrivatePortfolioLoggedInAsNotCreator() {
        login("39011220011");

        Portfolio portfolio = new Portfolio();
        portfolio.setId(7L);

        Response response = doPost(PORTFOLIO_COPY_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void copyPrivatePortfolioLoggedInAsCreator() {
        login("38011550077");
        Long userId = 2L;

        Portfolio portfolio = new Portfolio();
        portfolio.setId(7L);

        Response response = doPost(PORTFOLIO_COPY_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Portfolio copiedPortfolio = response.readEntity(Portfolio.class);
        assertEquals(userId, copiedPortfolio.getOriginalCreator().getId());
    }

    @Test
    public void deletePortfolioAsCreator() {
        login("89012378912");

        Portfolio portfolio = new Portfolio();
        portfolio.setId(12L);

        Response response = doPost(DELETE_PORTFOLIO_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void deletePortfolioAsAdmin() {
        login("89898989898");

        Portfolio portfolio = new Portfolio();
        portfolio.setId(13L);

        Response response = doPost(DELETE_PORTFOLIO_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void deletePortfolioAsNotCreator() {
        login("89012378912");

        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);

        Response response = doPost(DELETE_PORTFOLIO_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void deletePortfolioNotLoggedIn() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);

        Response response = doPost(DELETE_PORTFOLIO_URL, Entity.entity(portfolio, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    private Portfolio getPortfolio(long id) {
        return doGet(format(GET_PORTFOLIO_URL, id), Portfolio.class);
    }

    private void assertPortfolio1(Portfolio portfolio) {
        assertNotNull(portfolio);
        assertEquals(Long.valueOf(1), portfolio.getId());
        assertEquals("The new stock market", portfolio.getTitle());
        assertEquals(new DateTime("2000-12-29T08:00:01.000+02:00"), portfolio.getCreated());
        assertEquals(new DateTime("2004-12-29T08:00:01.000+02:00"), portfolio.getUpdated());
        assertEquals("Mathematics", portfolio.getTaxon().getName());
        assertEquals(new Long(6), portfolio.getCreator().getId());
        assertEquals("mati.maasikas-vaarikas", portfolio.getCreator().getUsername());
        assertEquals(new Long(5), portfolio.getOriginalCreator().getId());
        assertEquals("The changes after 2008.", portfolio.getSummary());
        assertEquals(new Long(95455215), portfolio.getViews());
        assertEquals(5, portfolio.getTags().size());

        List<Chapter> chapters = portfolio.getChapters();
        assertEquals(3, chapters.size());
        Chapter chapter = chapters.get(0);
        assertEquals(new Long(1), chapter.getId());
        assertEquals("The crisis", chapter.getTitle());
        assertNull(chapter.getText());
        List<Material> materials = chapter.getMaterials();
        assertEquals(1, materials.size());
        assertEquals(new Long(1), materials.get(0).getId());
        assertEquals(2, chapter.getSubchapters().size());
        Chapter subchapter1 = chapter.getSubchapters().get(0);
        assertEquals(new Long(4), subchapter1.getId());
        assertEquals("Subprime", subchapter1.getTitle());
        assertNull(subchapter1.getText());
        materials = subchapter1.getMaterials();
        assertEquals(3, materials.size());
        assertEquals(new Long(5), materials.get(0).getId());
        assertEquals(new Long(1), materials.get(1).getId());
        assertEquals(new Long(8), materials.get(2).getId());
        Chapter subchapter2 = chapter.getSubchapters().get(1);
        assertEquals(new Long(5), subchapter2.getId());
        assertEquals("The big crash", subchapter2.getTitle());
        assertEquals("Bla bla bla\nBla bla bla bla bla bla bla", subchapter2.getText());
        materials = subchapter2.getMaterials();
        assertEquals(1, materials.size());
        assertEquals(new Long(3), materials.get(0).getId());

        chapter = chapters.get(1);
        assertEquals(new Long(3), chapter.getId());
        assertEquals("Chapter 2", chapter.getTitle());
        assertEquals("Paragraph 1\n\nParagraph 2\n\nParagraph 3\n\nParagraph 4", chapter.getText());
        assertEquals(0, chapter.getMaterials().size());
        assertEquals(0, chapter.getSubchapters().size());

        chapter = chapters.get(2);
        assertEquals(new Long(2), chapter.getId());
        assertEquals("Chapter 3", chapter.getTitle());
        assertEquals("This is some text that explains what is the Chapter 3 about.\nIt can have many lines\n\n\n"
                + "And can also have    spaces   betwenn    the words on it", chapter.getText());
        assertEquals(0, chapter.getMaterials().size());
        assertEquals(0, chapter.getSubchapters().size());

        assertEquals(2, portfolio.getTargetGroups().size());
        assertTrue(portfolio.getTargetGroups().contains(TargetGroup.ZERO_FIVE));
        assertTrue(portfolio.getTargetGroups().contains(TargetGroup.SIX_SEVEN));
        assertEquals("Lifelong_learning_and_career_planning", portfolio.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", portfolio.getKeyCompetences().get(0).getName());
        assertEquals(Visibility.PUBLIC, portfolio.getVisibility());
        assertFalse(portfolio.isDeleted());
    }
}
