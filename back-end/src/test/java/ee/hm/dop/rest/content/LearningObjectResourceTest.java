package ee.hm.dop.rest.content;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.*;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static ee.hm.dop.rest.content.MaterialResourceTest.GET_MATERIAL_URL;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.junit.Assert.*;

public class LearningObjectResourceTest extends ResourceIntegrationTestBase {

    public static final String INCREASE_VIEW_COUNT_URL = "learningObject/increaseViewCount";
    public static final String ADD_TAG_URL = "learningObject/%s/tags";
    public static final String ADD_SYSTEM_TAG_URL = "learningObject/%s/system_tags";
    public static final String SET_TO_FAVOURITE_URL = "learningObject/favorite";
    public static final String GET_FAVOURITE_URL = "learningObject/favorite?id=%s";
    public static final String DELETE_FAVOURITE_URL = "learningObject/favorite/delete";
    public static final String USERS_FAVOURITE_URL = "learningObject/usersFavorite?start=0";
    public static final String GET_FAVOURITE_COUNT_URL = "learningObject/usersFavorite/count";
    public static final String LIKE_URL = "learningObject/like";
    public static final String DISLIKE_URL = "learningObject/dislike";
    public static final String GET_USER_LIKE_URL = "learningObject/getUserLike";
    public static final String REMOVE_USER_LIKE_URL = "learningObject/removeUserLike";
    public static final String TEST_TAG = "timshel";
    public static final String TEST_TAG_2 = "timshel2";
    public static final String TEST_SYSTEM_TAG = "matemaatika";

    @Test
    public void adding_tag_to_learning_object_adds_a_tag() {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(PORTFOLIO_8);
        assertFalse("Tag name", portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG)));

        Response response = doPut(format(ADD_TAG_URL, PORTFOLIO_8), tag(TEST_TAG));
        assertEquals("Add regular tag", Status.OK.getStatusCode(), response.getStatus());

        Portfolio portfolioAfter = getPortfolio(PORTFOLIO_8);
        assertTrue("Tag name", portfolioAfter.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG)));
    }

    @Test
    public void cannot_add_a_tag_to_learning_object_what_does_not_exist() {
        login(USER_PEETER);
        Response response = doPut(format(ADD_TAG_URL, (Long) NOT_EXISTS_ID), tag(TEST_TAG));
        assertEquals("Add regular tag", Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Ignore
    @Test
    public void adding_system_tag_adds_a_tag() throws Exception {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(PORTFOLIO_8);
        assertFalse("System tag name", portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_SYSTEM_TAG)));

        doPut(format(ADD_SYSTEM_TAG_URL, PORTFOLIO_8), tag(TEST_SYSTEM_TAG));

        Portfolio portfolioAfter = getPortfolio(PORTFOLIO_8);
        assertTrue("System tag name", portfolioAfter.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_SYSTEM_TAG)));
    }

    @Test
    public void adding_tag_with_same_name_throws_an_error() throws Exception {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(PORTFOLIO_8);
        assertFalse("Tag  name", portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG_2)));

        Response response = doPut(format(ADD_TAG_URL, PORTFOLIO_8), tag(TEST_TAG_2));
        assertEquals("Add regular tag", Status.OK.getStatusCode(), response.getStatus());

        Response response2 = doPut(format(ADD_TAG_URL, PORTFOLIO_8), tag(TEST_TAG_2));
        assertEquals("Add tag with same name", Status.INTERNAL_SERVER_ERROR.getStatusCode(), response2.getStatus());
    }

    @Test
    public void addUserFavorite_adds_learningObject_to_user_favorites() throws Exception {
        login(USER_PEETER);

        LearningObject learningObject = getPortfolio(PORTFOLIO_8);
        Response response = doPost(SET_TO_FAVOURITE_URL, learningObject);
        assertEquals("Set to favourite", Status.OK.getStatusCode(), response.getStatus());

        UserFavorite userFavorite = doGet(format(GET_FAVOURITE_URL, PORTFOLIO_8), UserFavorite.class);
        assertNotNull("User favourite exist", userFavorite);

        long favouritesCount = doGet(GET_FAVOURITE_COUNT_URL, Long.class);
        assertEquals("User favourite count", 1, favouritesCount);

        doPost(DELETE_FAVOURITE_URL, portfolioWithId(PORTFOLIO_8));
    }

    @Test
    public void cannot_find_user_favorite_when_it_is_not_set() throws Exception {
        login(USER_PEETER);

        UserFavorite userFavorite = doGet(format(GET_FAVOURITE_URL, PORTFOLIO_8), UserFavorite.class);
        assertNull("User favourite doesn't exist", userFavorite);
    }

    @Test
    public void deleting_user_favorite_removes_it_from_user_favorites() throws Exception {
        login(USER_PEETER);

        LearningObject learningObject = getPortfolio(PORTFOLIO_8);

        doPost(SET_TO_FAVOURITE_URL, learningObject);
        doGet(format(GET_FAVOURITE_URL, PORTFOLIO_8), UserFavorite.class);
        doPost(DELETE_FAVOURITE_URL, portfolioWithId(PORTFOLIO_8));

        SearchResult searchResult = doGet(USERS_FAVOURITE_URL, SearchResult.class);
        assertTrue("User favourites doesn't exist", isEmpty(searchResult.getItems()));
    }

    @Test
    public void likeMaterial_sets_it_as_liked() throws Exception {
        login(USER_PEETER);
        Material material = getMaterial(MATERIAL_5);

        doPost(LIKE_URL, material);
        UserLike userLike = doPost(GET_USER_LIKE_URL, material, UserLike.class);
        assertNotNull("User like exist", userLike);
        assertEquals("Material is liked by user", true, userLike.isLiked());
    }

    @Test
    public void dislikeMaterial_sets_it_as_not_liked() throws Exception {
        login(USER_PEETER);
        Material material = getMaterial(MATERIAL_5);

        doPost(DISLIKE_URL, material);
        UserLike userDislike = doPost(GET_USER_LIKE_URL, material, UserLike.class);
        assertNotNull("User dislike exist", userDislike);
        assertEquals("Material is disliked by user", false, userDislike.isLiked());
    }

    @Test
    public void removeUserLike_removes_like_from_material() throws Exception {
        login(USER_PEETER);
        Material material = getMaterial(MATERIAL_5);

        doPost(LIKE_URL, material);
        doPost(REMOVE_USER_LIKE_URL, material);
        UserLike userRemoveLike = doPost(GET_USER_LIKE_URL, material, UserLike.class);
        assertNull("User removed like does not exist", userRemoveLike);
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


    @Test
    public void increaseViewCount_material() {
        Material materialBefore = getMaterial(MATERIAL_5);

        Response response = doPost(INCREASE_VIEW_COUNT_URL, materialWithId(MATERIAL_5));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Material materialAfter = getMaterial(MATERIAL_5);
        assertEquals(Long.valueOf(materialBefore.getViews() + 1), materialAfter.getViews());
    }

    @Test
    public void increaseViewCountNotExistingMaterial() {
        Response response = doGet(format(GET_MATERIAL_URL, (long) NOT_EXISTS_ID));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doPost(INCREASE_VIEW_COUNT_URL, materialWithId(NOT_EXISTS_ID));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        response = doGet(format(GET_MATERIAL_URL, (long) NOT_EXISTS_ID));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }


    @Test
    public void increaseViewCount_portfolio() {
        Portfolio portfolioBefore = getPortfolio(PORTFOLIO_3);
        doPost(INCREASE_VIEW_COUNT_URL, portfolioWithId(PORTFOLIO_3));
        Portfolio portfolioAfter = getPortfolio(PORTFOLIO_3);
        assertEquals(Long.valueOf(portfolioBefore.getViews() + 1), portfolioAfter.getViews());
    }

    @Test
    public void increaseViewCountNoPortfolio() {
        Response response = doPost(INCREASE_VIEW_COUNT_URL, portfolioWithId(99999L));
        assertEquals(500, response.getStatus());
    }
}
