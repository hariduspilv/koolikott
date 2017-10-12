package ee.hm.dop.rest.content;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestLayer;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Visibility;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class PortfolioResourceTest extends ResourceIntegrationTestBase {

    private static final String CREATE_PORTFOLIO_URL = "portfolio/create";
    private static final String UPDATE_PORTFOLIO_URL = "portfolio/update";
    public static final String GET_PORTFOLIO_URL = "portfolio?id=%s";
    private static final String GET_BY_CREATOR_URL = "portfolio/getByCreator?username=%s";
    private static final String GET_BY_CREATOR_COUNT_URL = "portfolio/getByCreator/count?username=%s";
    private static final String PORTFOLIO_INCREASE_VIEW_COUNT_URL = "portfolio/increaseViewCount";
    private static final String PORTFOLIO_COPY_URL = "portfolio/copy";
    private static final String DELETE_PORTFOLIO_URL = "portfolio/delete";
    private static final String LIKE_URL = "portfolio/like";
    private static final String DISLIKE_URL = "portfolio/dislike";
    private static final String GET_USER_LIKE_URL = "portfolio/getUserLike";
    private static final String REMOVE_USER_LIKE_URL = "portfolio/removeUserLike";

    private static final String CREATE_MATERIAL_URL = "material";
    public static final String NEW_SUBCHAPTER = "New subchapter";
    public static final String NEW_CHAPTER_1 = "New chapter 1";
    public static final String NEW_COOL_SUBCHAPTER = "New cool subchapter";

    @Test
    public void getPortfolio() {
        assertPortfolio1(getPortfolio(PORTFOLIO_1), TestLayer.REST);
    }

    @Test
    public void getNotExistingPortfolio() {
        Response response = doGet(format(GET_PORTFOLIO_URL, 2000));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPrivatePortfolioAsCreator() {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(PORTFOLIO_7);
        assertEquals(PORTFOLIO_7, portfolio.getId());
        assertEquals("This portfolio is private. ", portfolio.getTitle());
    }

    @Test
    public void getPrivatePortfolioAsNotCreator() {
        login(USER_VOLDERMAR2);

        Response response = doGet(format(GET_PORTFOLIO_URL, PORTFOLIO_7));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPrivatePortfolioAsAdmin() {
        login(USER_ADMIN);

        Portfolio portfolio = getPortfolio(PORTFOLIO_7);
        assertEquals(PORTFOLIO_7, portfolio.getId());
        assertEquals("This portfolio is private. ", portfolio.getTitle());
    }

    @Test
    public void getByCreator() {
        SearchResult result = doGet(format(GET_BY_CREATOR_URL, USER_MAASIKAS_VAARIKAS.username)).readEntity(SearchResult.class);
        List<Searchable> portfolios = result.getItems();
        assertEquals(3, portfolios.size());

        List<Long> actualIds = portfolios.stream().map(Searchable::getId).collect(Collectors.toList());
        List<Long> expectedIds = Arrays.asList(PORTFOLIO_1, PORTFOLIO_3, PORTFOLIO_14);
        assertTrue(actualIds.containsAll(expectedIds));
    }

    @Test
    public void getByCreatorCount_returns_same_portfolios_count_as_getByCreator_size() throws Exception {
        List<Searchable> portfolios = doGet(format(GET_BY_CREATOR_URL, USER_MAASIKAS_VAARIKAS.username), SearchResult.class).getItems();
        long count = doGet(format(GET_BY_CREATOR_COUNT_URL, USER_MAASIKAS_VAARIKAS.username), Long.class);
        assertEquals("Portfolios size by creator, Portfolios count by creator", portfolios.size(), count);
    }

    @Test
    public void getByCreatorWhenSomeArePrivateOrNotListed() {
        List<Searchable> portfolios = doGet(format(GET_BY_CREATOR_URL, USER_MYTESTUSER.username), SearchResult.class).getItems();

        assertEquals(1, portfolios.size());
        assertEquals(PORTFOLIO_9, portfolios.get(0).getId());
    }

    @Test
    public void getByCreatorWhenSomeArePrivateOrNotListedAsCreator() {
        login(USER_MYTESTUSER);
        assertGetByCreator();
    }

    @Test
    public void getByCreatorWhenSomeArePrivateOrNotListedAsAdmin() {
        login(USER_ADMIN);
        assertGetByCreator();
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
        assertEquals("User does not exist with this username parameter", response.readEntity(String.class));
    }

    @Test
    public void getByCreatorNoMaterials() {
        SearchResult portfolios = doGet(format(GET_BY_CREATOR_URL, USER_VOLDERMAR.username), SearchResult.class);

        assertEquals(0, portfolios.getItems().size());
        assertEquals(0, portfolios.getStart());
        assertEquals(0, portfolios.getTotalResults());
    }

    @Test
    public void increaseViewCount() {
        Portfolio portfolioBefore = getPortfolio(PORTFOLIO_3);
        doPost(PORTFOLIO_INCREASE_VIEW_COUNT_URL, portfolioWithId(PORTFOLIO_3));
        Portfolio portfolioAfter = getPortfolio(PORTFOLIO_3);
        assertEquals(Long.valueOf(portfolioBefore.getViews() + 1), portfolioAfter.getViews());
    }

    @Test
    public void increaseViewCountNoPortfolio() {
        Response response = doPost(PORTFOLIO_INCREASE_VIEW_COUNT_URL, portfolioWithId(99999L));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void create() {
        login(USER_MATI);
        Portfolio createdPortfolio = createPortfolio();
        assertNotNull(createdPortfolio);
        assertNotNull(createdPortfolio.getId());
        assertEquals((Long) 1L, createdPortfolio.getOriginalCreator().getId());
        assertEquals((Long) 1L, createdPortfolio.getCreator().getId());
    }

    @Test
    public void updateChanginMetadataNoChapters() {
        login(USER_MATI);

        Portfolio portfolio = getPortfolio(PORTFOLIO_5);
        String originalTitle = portfolio.getTitle();
        portfolio.setTitle("New mega nice title that I come with Yesterday night!");

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(originalTitle.equals(updatedPortfolio.getTitle()));
        assertEquals("New mega nice title that I come with Yesterday night!", updatedPortfolio.getTitle());
    }

    @Test
    public void updateSomeoneElsesPortfolio() {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(105);
        portfolio.setTitle("This is not my portfolio.");

        portfolio.setCreator(userWithId(USER_PEETER.id));

        Response response = doPost(UPDATE_PORTFOLIO_URL, portfolio);
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateCreatingChapter() {
        login(USER_MATI);

        List<Chapter> chapters = chapters(NEW_CHAPTER_1);

        Portfolio portfolio = getPortfolio(PORTFOLIO_5);
        portfolio.setChapters(chapters);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);
        assertFalse(updatedPortfolio.getChapters().isEmpty());
    }

    @Test
    public void updateCreatingChapterWithSubchapterNoMaterials() {
        login(USER_MATI);

        List<Chapter> chapters = new ArrayList<>();
        Chapter newChapter = chapter(NEW_CHAPTER_1);
        newChapter.setSubchapters(chapters(NEW_SUBCHAPTER));

        chapters.add(newChapter);

        Portfolio portfolio = getPortfolio(PORTFOLIO_5);
        portfolio.setChapters(chapters);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(updatedPortfolio.getChapters().isEmpty());
        assertFalse(updatedPortfolio.getChapters().get(0).getSubchapters().isEmpty());
    }

    @Test
    public void updateCreatingChapterWithExistingChapter() {
        login(USER_MATI);

        Chapter newChapter = chapter(NEW_CHAPTER_1);
        newChapter.setSubchapters(chapters(NEW_COOL_SUBCHAPTER));

        Portfolio portfolio = getPortfolio(PORTFOLIO_5);
        portfolio.getChapters().add(newChapter);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(updatedPortfolio.getChapters().isEmpty());
        Chapter verify = updatedPortfolio.getChapters().get(updatedPortfolio.getChapters().size() - 1).getSubchapters().get(0);
        assertEquals(NEW_COOL_SUBCHAPTER, verify.getTitle());
    }

    @Test
    public void updateChangingVisibility() {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(PORTFOLIO_6);
        portfolio.setVisibility(Visibility.NOT_LISTED);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertEquals(Visibility.NOT_LISTED, updatedPortfolio.getVisibility());
    }

    @Test
    public void copyPortfolio() {
        login(USER_PEETER);

        Portfolio copiedPortfolio = doPost(PORTFOLIO_COPY_URL, portfolioWithId(PORTFOLIO_1), Portfolio.class);
        assertNotNull(copiedPortfolio);
        assertEquals(Long.valueOf(2), copiedPortfolio.getCreator().getId());
        assertEquals(Long.valueOf(6), copiedPortfolio.getOriginalCreator().getId());
    }

    @Test
    public void copyPrivatePortfolioNotLoggedIn() {
        Response response = doPost(PORTFOLIO_COPY_URL, portfolioWithId(PORTFOLIO_7));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void copyPrivatePortfolioLoggedInAsNotCreator() {
        login(USER_MATI);
        Response response = doPost(PORTFOLIO_COPY_URL, portfolioWithId(PORTFOLIO_7));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void copyPrivatePortfolioLoggedInAsCreator() {
        login(USER_PEETER);

        Response response = doPost(PORTFOLIO_COPY_URL, portfolioWithId(PORTFOLIO_7));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        Portfolio copiedPortfolio = response.readEntity(Portfolio.class);
        assertEquals(USER_PEETER.id, copiedPortfolio.getOriginalCreator().getId());
    }

    @Test
    public void deletePortfolioAsCreator() {
        login(USER_SECOND);
        Response response = doPost(DELETE_PORTFOLIO_URL, portfolioWithId(PORTFOLIO_12));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void deletePortfolioAsNotCreator() {
        login(USER_SECOND);
        Response response = doPost(DELETE_PORTFOLIO_URL, portfolioWithId(PORTFOLIO_1));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void deletePortfolioNotLoggedIn() {
        Response response = doPost(DELETE_PORTFOLIO_URL, portfolioWithId(PORTFOLIO_1));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void createWithContent() {
        login(USER_MATI);

        Portfolio portfolio = portfolioWithTitle("With chapters");

        List<Chapter> chapters = new ArrayList<>();
        Chapter firstChapter = chapter("First chapter");

        List<LearningObject> learningObjects = new ArrayList<>();
        ChapterObject chapterObject = new ChapterObject();
        chapterObject.setText("Random textbox content");
        learningObjects.add(chapterObject);

        Material material = new Material();
        material.setSource("http://www.november.juliet.ru");

        Material createdMaterial = doPut(CREATE_MATERIAL_URL, material, Material.class);
        learningObjects.add(createdMaterial);

        List<ContentRow> contentRows = new ArrayList<>();
        contentRows.add(new ContentRow(learningObjects));

        firstChapter.setContentRows(contentRows);
        chapters.add(firstChapter);
        portfolio.setChapters(chapters);

        Portfolio createdPortfolio = doPost(CREATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertNotNull(createdPortfolio);
        assertNotNull(createdPortfolio.getId());
        assertEquals(((ChapterObject) createdPortfolio.getChapters().get(0).getContentRows().get(0).getLearningObjects().get(0)).getText(), chapterObject.getText());
        assertEquals(((Material) createdPortfolio.getChapters().get(0).getContentRows().get(0).getLearningObjects().get(1)).getSource(), createdMaterial.getSource());
    }

    @Test
    public void likePortfolio_sets_it_as_liked() throws Exception {
        login(USER_PEETER);
        Portfolio portfolio = getPortfolio(PORTFOLIO_3);

        doPost(LIKE_URL, portfolio);
        UserLike userLike = doPost(GET_USER_LIKE_URL, portfolio, UserLike.class);
        assertNotNull("User like exist", userLike);
        assertEquals("Portfolio is liked by user", true, userLike.isLiked());
    }

    @Test
    public void dislikePortfolio_sets_it_as_not_liked() throws Exception {
        login(USER_PEETER);
        Portfolio portfolio = getPortfolio(PORTFOLIO_3);

        doPost(DISLIKE_URL, portfolio);
        UserLike userDislike = doPost(GET_USER_LIKE_URL, portfolio, UserLike.class);
        assertNotNull("User dislike exist", userDislike);
        assertEquals("Portfolio is disliked by user", false, userDislike.isLiked());
    }

    @Test
    public void removeUserLike_removes_like_from_portfolio() throws Exception {
        login(USER_PEETER);
        Portfolio portfolio = getPortfolio(PORTFOLIO_3);

        doPost(LIKE_URL, portfolio);
        doPost(REMOVE_USER_LIKE_URL, portfolio);
        UserLike userRemoveLike = doPost(GET_USER_LIKE_URL, portfolio, UserLike.class);
        assertNull("User removed like does not exist", userRemoveLike);
    }

    private Portfolio createPortfolio() {
        return doPost(CREATE_PORTFOLIO_URL, portfolioWithTitle("Tere"), Portfolio.class);
    }

    private void assertGetByCreator() {
        SearchResult result = doGet(format(GET_BY_CREATOR_URL, USER_MYTESTUSER.username), SearchResult.class);
        List<Searchable> portfolios = result.getItems();

        assertEquals(3, portfolios.size());
        List<Long> expectedIds = Arrays.asList(PORTFOLIO_9, PORTFOLIO_10, PORTFOLIO_11);
        List<Long> actualIds = portfolios.stream().map(Searchable::getId).collect(Collectors.toList());
        assertTrue(actualIds.containsAll(expectedIds));
    }

    private Chapter chapter(String title) {
        Chapter newChapter = new Chapter();
        newChapter.setTitle(title);
        return newChapter;
    }

    private List<Chapter> chapters(String title) {
        List<Chapter> subchapters = new ArrayList<>();
        subchapters.add(chapter(title));
        return subchapters;
    }

    private Portfolio portfolioWithTitle(String title) {
        Portfolio portfolio = new Portfolio();
        portfolio.setTitle(title);
        return portfolio;
    }
}
