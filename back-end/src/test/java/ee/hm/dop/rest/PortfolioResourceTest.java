package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.enums.Visibility;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
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
    private static final String PORTFOLIO_ADD_RECOMMENDATION_URL = "portfolio/recommend";
    private static final String PORTFOLIO_REMOVE_RECOMMENDATION_URL = "portfolio/removeRecommendation";
    private static final String LIKE_URL = "portfolio/like";
    private static final String DISLIKE_URL = "portfolio/dislike";
    private static final String GET_USER_LIKE_URL = "portfolio/getUserLike";
    private static final String REMOVE_USER_LIKE_URL = "portfolio/removeUserLike";

    private static final String RESTORE_PORTFOLIO = "admin/deleted/portfolio/restore";
    private static final String GET_DELETED_PORTFOLIOS = "admin/deleted/portfolio/getDeleted";
    private static final String GET_DELETED_PORTFOLIOS_COUNT = "admin/deleted/portfolio/getDeleted/count";

    private static final String CREATE_MATERIAL_URL = "material";
    public static final String NEW_SUBCHAPTER = "New subchapter";
    public static final String NEW_CHAPTER_1 = "New chapter 1";
    public static final String NEW_COOL_SUBCHAPTER = "New cool subchapter";

    @Test
    public void getPortfolio() {
        assertPortfolio(getPortfolio(TestConstants.PORTFOLIO_1));
    }

    @Test
    public void getNotExistingPortfolio() {
        Response response = doGet(format(GET_PORTFOLIO_URL, 2000));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPrivatePortfolioAsCreator() {
        login(TestConstants.USER_PEETER);

        Portfolio portfolio = getPortfolio(TestConstants.PORTFOLIO_7);
        assertEquals(TestConstants.PORTFOLIO_7, portfolio.getId());
        assertEquals("This portfolio is private. ", portfolio.getTitle());
    }

    @Test
    public void getPrivatePortfolioAsNotCreator() {
        login(TestConstants.USER_VOLDERMAR2);

        //todo why is material here
        Response response = doGet(format(GET_PORTFOLIO_URL, (Long) 7L));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void getPrivatePortfolioAsAdmin() {
        login(TestConstants.USER_ADMIN);

        Portfolio portfolio = getPortfolio(TestConstants.PORTFOLIO_7);
        assertEquals(TestConstants.PORTFOLIO_7, portfolio.getId());
        assertEquals("This portfolio is private. ", portfolio.getTitle());
    }

    @Test
    public void getByCreator() {
        String username = "mati.maasikas-vaarikas";
        SearchResult result = doGet(format(GET_BY_CREATOR_URL, username)).readEntity(SearchResult.class);
        List<Searchable> portfolios = result.getItems();
        assertEquals(3, portfolios.size());

        List<Long> actualIds = portfolios.stream().map(Searchable::getId).collect(Collectors.toList());
        List<Long> expectedIds = Arrays.asList(TestConstants.PORTFOLIO_1, TestConstants.PORTFOLIO_3, TestConstants.PORTFOLIO_14);
        assertTrue(actualIds.containsAll(expectedIds));
    }

    @Test
    public void getByCreatorCount_returns_same_portfolios_count_as_getByCreator_size() throws Exception {
        List<Searchable> portfolios = doGet(format(GET_BY_CREATOR_URL, "mati.maasikas-vaarikas")).readEntity(SearchResult.class).getItems();
        long count = doGet(format(GET_BY_CREATOR_COUNT_URL, "mati.maasikas-vaarikas"), Long.class);
        assertEquals("Portfolios size by creator, Portfolios count by creator", portfolios.size(), count);
    }

    @Test
    public void getByCreatorWhenSomeArePrivateOrNotListed() {
        String username = "my.testuser";
        SearchResult result = doGet(format(GET_BY_CREATOR_URL, username), SearchResult.class);
        List<Searchable> portfolios = result.getItems();

        assertEquals(1, portfolios.size());
        assertEquals(TestConstants.PORTFOLIO_9, portfolios.get(0).getId());
    }

    @Test
    public void getByCreatorWhenSomeArePrivateOrNotListedAsCreator() {
        login(TestConstants.USER_MYTESTUSER);
        assertGetByCreator();
    }

    @Test
    public void getByCreatorWhenSomeArePrivateOrNotListedAsAdmin() {
        login(TestConstants.USER_ADMIN);
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
        String username = "voldemar.vapustav";
        SearchResult portfolios = doGet(format(GET_BY_CREATOR_URL, username), SearchResult.class);

        assertEquals(0, portfolios.getItems().size());
        assertEquals(0, portfolios.getStart());
        assertEquals(0, portfolios.getTotalResults());
    }

    @Test
    public void increaseViewCount() {
        Portfolio portfolioBefore = getPortfolio(TestConstants.PORTFOLIO_3);
        doPost(PORTFOLIO_INCREASE_VIEW_COUNT_URL, portfolioWithId(TestConstants.PORTFOLIO_3));
        Portfolio portfolioAfter = getPortfolio(TestConstants.PORTFOLIO_3);
        assertEquals(Long.valueOf(portfolioBefore.getViews() + 1), portfolioAfter.getViews());
    }

    @Test
    public void increaseViewCountNoPortfolio() {
        Response response = doPost(PORTFOLIO_INCREASE_VIEW_COUNT_URL, portfolioWithId(99999L));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void create() {
        login(TestConstants.USER_MATI);
        Portfolio createdPortfolio = createPortfolio();
        assertNotNull(createdPortfolio);
        assertNotNull(createdPortfolio.getId());
        assertEquals((Long) 1L, createdPortfolio.getOriginalCreator().getId());
        assertEquals((Long) 1L, createdPortfolio.getCreator().getId());
    }

    @Test
    public void updateChanginMetadataNoChapters() {
        login(TestConstants.USER_MATI);

        Portfolio portfolio = getPortfolio(105);
        String originalTitle = portfolio.getTitle();
        portfolio.setTitle("New mega nice title that I come with Yesterday night!");

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(originalTitle.equals(updatedPortfolio.getTitle()));
        assertEquals("New mega nice title that I come with Yesterday night!", updatedPortfolio.getTitle());

    }

    @Test
    public void updateSomeoneElsesPortfolio() {
        login(TestConstants.USER_PEETER);

        Portfolio portfolio = getPortfolio(105);
        portfolio.setTitle("This is not my portfolio.");

        // 2L is peeter id
        portfolio.setCreator(userWithId(2L));

        Response response = doPost(UPDATE_PORTFOLIO_URL, portfolio);
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateCreatingChapter() {
        login(TestConstants.USER_MATI);

        List<Chapter> chapters = chapters(NEW_CHAPTER_1);

        Portfolio portfolio = getPortfolio(105);
        portfolio.setChapters(chapters);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);
        assertFalse(updatedPortfolio.getChapters().isEmpty());

    }

    @Test
    public void updateCreatingChapterWithSubchapterNoMaterials() {
        login(TestConstants.USER_MATI);

        List<Chapter> chapters = new ArrayList<>();
        Chapter newChapter = chapter(NEW_CHAPTER_1);
        newChapter.setSubchapters(chapters(NEW_SUBCHAPTER));

        chapters.add(newChapter);

        Portfolio portfolio = getPortfolio(105);
        portfolio.setChapters(chapters);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(updatedPortfolio.getChapters().isEmpty());
        assertFalse(updatedPortfolio.getChapters().get(0).getSubchapters().isEmpty());
    }

    @Test
    public void updateCreatingChapterWithExistingChapter() {
        login(TestConstants.USER_MATI);

        Chapter newChapter = chapter(NEW_CHAPTER_1);
        newChapter.setSubchapters(chapters(NEW_COOL_SUBCHAPTER));

        Portfolio portfolio = getPortfolio(105);
        portfolio.getChapters().add(newChapter);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertFalse(updatedPortfolio.getChapters().isEmpty());
        Chapter verify = updatedPortfolio.getChapters().get(updatedPortfolio.getChapters().size() - 1).getSubchapters().get(0);
        assertEquals(NEW_COOL_SUBCHAPTER, verify.getTitle());

    }

    @Test
    public void updateChangingVisibility() {
        login(TestConstants.USER_PEETER);

        Portfolio portfolio = getPortfolio(106);
        portfolio.setVisibility(Visibility.NOT_LISTED);

        Portfolio updatedPortfolio = doPost(UPDATE_PORTFOLIO_URL, portfolio, Portfolio.class);

        assertEquals(Visibility.NOT_LISTED, updatedPortfolio.getVisibility());
    }

    @Test
    public void copyPortfolio() {
        login(TestConstants.USER_PEETER);

        Portfolio copiedPortfolio = doPost(PORTFOLIO_COPY_URL, portfolioWithId(TestConstants.PORTFOLIO_1), Portfolio.class);
        assertNotNull(copiedPortfolio);
        assertEquals(Long.valueOf(2), copiedPortfolio.getCreator().getId());
        assertEquals(Long.valueOf(6), copiedPortfolio.getOriginalCreator().getId());
    }

    @Test
    public void copyPrivatePortfolioNotLoggedIn() {
        Response response = doPost(PORTFOLIO_COPY_URL, portfolioWithId(TestConstants.PORTFOLIO_7));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void copyPrivatePortfolioLoggedInAsNotCreator() {
        login(TestConstants.USER_MATI);
        Response response = doPost(PORTFOLIO_COPY_URL, portfolioWithId(TestConstants.PORTFOLIO_7));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void copyPrivatePortfolioLoggedInAsCreator() {
        login(TestConstants.USER_PEETER);

        Response response = doPost(PORTFOLIO_COPY_URL, portfolioWithId(TestConstants.PORTFOLIO_7));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        Portfolio copiedPortfolio = response.readEntity(Portfolio.class);
        assertEquals((Long) 2L, copiedPortfolio.getOriginalCreator().getId());
    }

    @Test
    public void deletePortfolioAsCreator() {
        login(TestConstants.USER_SECOND);
        Response response = doPost(DELETE_PORTFOLIO_URL, portfolioWithId(TestConstants.PORTFOLIO_12));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void deletePortfolioAsAdmin() {
        login(TestConstants.USER_ADMIN);
        Response response = doPost(DELETE_PORTFOLIO_URL, portfolioWithId(TestConstants.PORTFOLIO_13));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void deletePortfolioAsNotCreator() {
        login(TestConstants.USER_SECOND);
        Response response = doPost(DELETE_PORTFOLIO_URL, portfolioWithId(TestConstants.PORTFOLIO_1));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void deletePortfolioNotLoggedIn() {
        Response response = doPost(DELETE_PORTFOLIO_URL, portfolioWithId(TestConstants.PORTFOLIO_1));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void recommendPortfolio() {
        login(TestConstants.USER_PEETER);
        Portfolio portfolio = getPortfolio(TestConstants.PORTFOLIO_3);
        Response response = doPost(PORTFOLIO_ADD_RECOMMENDATION_URL, portfolio);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());

        logout();

        login(TestConstants.USER_ADMIN);
        Response responseAdmin = doPost(PORTFOLIO_ADD_RECOMMENDATION_URL, portfolio);
        assertEquals(Status.OK.getStatusCode(), responseAdmin.getStatus());
        assertNotNull(responseAdmin.readEntity(Recommendation.class));

        Portfolio portfolioAfterRecommend = getPortfolio(TestConstants.PORTFOLIO_3);
        assertNotNull(portfolioAfterRecommend.getRecommendation());
    }

    @Test
    public void removedPortfolioRecommendation() {
        login(TestConstants.USER_PEETER);
        Portfolio portfolio = getPortfolio(TestConstants.PORTFOLIO_3);
        Response response = doPost(PORTFOLIO_ADD_RECOMMENDATION_URL, portfolio);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());

        logout();

        login(TestConstants.USER_ADMIN);
        Response responseAdmin = doPost(PORTFOLIO_ADD_RECOMMENDATION_URL, portfolio);
        assertEquals(Status.OK.getStatusCode(), responseAdmin.getStatus());
        assertNotNull(responseAdmin.readEntity(Recommendation.class));
        Portfolio portfolioAfterRecommend = getPortfolio(TestConstants.PORTFOLIO_3);
        assertNotNull(portfolioAfterRecommend.getRecommendation());

        logout();

        login(TestConstants.USER_PEETER);
        Response responseRemoveRecommendation = doPost(PORTFOLIO_REMOVE_RECOMMENDATION_URL, portfolio);
        assertEquals(Status.FORBIDDEN.getStatusCode(), responseRemoveRecommendation.getStatus());
        logout();

        login(TestConstants.USER_ADMIN);
        Response responseRemoveRecommendationAdmin = doPost(PORTFOLIO_REMOVE_RECOMMENDATION_URL, portfolio);
        assertEquals(Status.NO_CONTENT.getStatusCode(), responseRemoveRecommendationAdmin.getStatus());

        Portfolio portfolioAfterRemoveRecommend = getPortfolio(TestConstants.PORTFOLIO_3);
        assertNull(portfolioAfterRemoveRecommend.getRecommendation());
    }

    @Test
    public void createWithContent() {
        login(TestConstants.USER_MATI);

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
        login(TestConstants.USER_PEETER);
        Portfolio portfolio = getPortfolio(TestConstants.PORTFOLIO_3);

        doPost(LIKE_URL, portfolio);
        UserLike userLike = doPost(GET_USER_LIKE_URL, portfolio, UserLike.class);
        assertNotNull("User like exist", userLike);
        assertEquals("Portfolio is liked by user", true, userLike.isLiked());
    }

    @Test
    public void dislikePortfolio_sets_it_as_not_liked() throws Exception {
        login(TestConstants.USER_PEETER);
        Portfolio portfolio = getPortfolio(TestConstants.PORTFOLIO_3);

        doPost(DISLIKE_URL, portfolio);
        UserLike userDislike = doPost(GET_USER_LIKE_URL, portfolio, UserLike.class);
        assertNotNull("User dislike exist", userDislike);
        assertEquals("Portfolio is disliked by user", false, userDislike.isLiked());
    }

    @Test
    public void removeUserLike_removes_like_from_portfolio() throws Exception {
        login(TestConstants.USER_PEETER);
        Portfolio portfolio = getPortfolio(TestConstants.PORTFOLIO_3);

        doPost(LIKE_URL, portfolio);
        doPost(REMOVE_USER_LIKE_URL, portfolio);
        UserLike userRemoveLike = doPost(GET_USER_LIKE_URL, portfolio, UserLike.class);
        assertNull("User removed like does not exist", userRemoveLike);
    }

    @Test
    public void restoring_deleted_portfolio_restores_it() throws Exception {
        login(TestConstants.USER_ADMIN);

        doPost(DELETE_PORTFOLIO_URL, portfolioWithId(TestConstants.PORTFOLIO_13));
        assertTrue("Portfolio is deleted", getPortfolio(TestConstants.PORTFOLIO_13).isDeleted());

        doPost(RESTORE_PORTFOLIO, portfolioWithId(TestConstants.PORTFOLIO_13));
        assertFalse("Portfolio is not deleted", getPortfolio(TestConstants.PORTFOLIO_13).isDeleted());
    }

    @Test
    public void getDeleted_returns_deleted_portfolios_to_user_admin() throws Exception {
        login(TestConstants.USER_ADMIN);
        List<Portfolio> deletedPortfolios = doGet(GET_DELETED_PORTFOLIOS, new GenericType<List<Portfolio>>() {
        });
        long deletedPortfoliosCount = doGet(GET_DELETED_PORTFOLIOS_COUNT, Long.class);

        assertTrue("Portfolios are deleted", deletedPortfolios.stream().allMatch(LearningObject::isDeleted));
        assertEquals("Deleted portfolios list size, deleted portfolios count", deletedPortfolios.size(), deletedPortfoliosCount);
    }

    @Test
    public void regular_user_do_not_have_access_to_get_deleted_portfolios() throws Exception {
        login(TestConstants.USER_PEETER);
        Response response = doGet(GET_DELETED_PORTFOLIOS);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    private Portfolio createPortfolio() {
        return doPost(CREATE_PORTFOLIO_URL, portfolioWithTitle("Tere"), Portfolio.class);
    }

    private void assertPortfolio(Portfolio portfolio) {
        assertNotNull(portfolio);
        assertEquals(TestConstants.PORTFOLIO_1, portfolio.getId());
        assertEquals("The new stock market", portfolio.getTitle());
        assertEquals(new DateTime("2000-12-29T08:00:01.000+02:00"), portfolio.getAdded());
        assertEquals(new DateTime("2004-12-29T08:00:01.000+02:00"), portfolio.getUpdated());
        assertEquals("Mathematics", portfolio.getTaxons().get(0).getName());
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
        List<LearningObject> materials = chapter.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(1), materials.get(0).getId());
        assertEquals(2, chapter.getSubchapters().size());
        Chapter subchapter1 = chapter.getSubchapters().get(0);
        assertEquals(new Long(4), subchapter1.getId());
        assertEquals("Subprime", subchapter1.getTitle());
        assertNull(subchapter1.getText());
        materials = subchapter1.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(8), materials.get(0).getId());
        Chapter subchapter2 = chapter.getSubchapters().get(1);
        assertEquals(new Long(5), subchapter2.getId());
        assertEquals("The big crash", subchapter2.getTitle());
        assertEquals("Bla bla bla\nBla bla bla bla bla bla bla", subchapter2.getText());
        materials = subchapter2.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(3), materials.get(0).getId());

        chapter = chapters.get(1);
        assertEquals(new Long(3), chapter.getId());
        assertEquals("Chapter 2", chapter.getTitle());
        assertEquals("Paragraph 1\n\nParagraph 2\n\nParagraph 3\n\nParagraph 4", chapter.getText());
        assertEquals(1, chapter.getContentRows().get(0).getLearningObjects().size());
        assertEquals(0, chapter.getSubchapters().size());

        chapter = chapters.get(2);
        assertEquals(new Long(2), chapter.getId());
        assertEquals("Chapter 3", chapter.getTitle());
        assertEquals("This is some text that explains what is the Chapter 3 about.\nIt can have many lines\n\n\n"
                + "And can also have    spaces   betwenn    the words on it", chapter.getText());
        assertEquals(1, chapter.getContentRows().get(0).getLearningObjects().size());
        assertEquals(0, chapter.getSubchapters().size());

        assertEquals(2, portfolio.getTargetGroups().size());
        assertTrue(TargetGroupEnum.containsTargetGroup(portfolio.getTargetGroups(), TargetGroupEnum.ZERO_FIVE));
        assertTrue(TargetGroupEnum.containsTargetGroup(portfolio.getTargetGroups(), TargetGroupEnum.SIX_SEVEN));
        assertEquals("Lifelong_learning_and_career_planning", portfolio.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", portfolio.getKeyCompetences().get(0).getName());
        assertEquals(Visibility.PUBLIC, portfolio.getVisibility());
        assertFalse(portfolio.isDeleted());

        Recommendation recommendation = portfolio.getRecommendation();
        assertNotNull(recommendation);
        assertEquals(Long.valueOf(3), recommendation.getId());
    }

    private void assertGetByCreator() {
        String username = "my.testuser";
        SearchResult result = doGet(format(GET_BY_CREATOR_URL, username), SearchResult.class);
        List<Searchable> portfolios = result.getItems();

        assertEquals(3, portfolios.size());
        List<Long> expectedIds = Arrays.asList(TestConstants.PORTFOLIO_9, TestConstants.PORTFOLIO_10, TestConstants.PORTFOLIO_11);
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
