package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.*;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.junit.Assert.*;

public class LearningObjectResourceTest extends ResourceIntegrationTestBase {

    public static final String ADD_TAG_URL = "learningObject/%s/tags";
    public static final String ADD_SYSTEM_TAG_URL = "learningObject/%s/system_tags?type=%s&name=%s";
    public static final String SET_TO_FAVOURITE_URL = "learningObject/favorite";
    public static final String GET_FAVOURITE_URL = "learningObject/favorite?id=%s";
    public static final String DELETE_FAVOURITE_URL = "learningObject/favorite?id=%s";
    public static final String USERS_FAVOURITE_URL = "learningObject/usersFavorite?start=0";
    public static final String GET_FAVOURITE_COUNT_URL = "learningObject/usersFavorite/count";
    public static final String TEST_TAG = "timshel";
    public static final String TEST_TAG_2 = "timshel2";
    public static final String TEST_SYSTEM_TAG = "matemaatika";
    public static final String TYPE_PORTFOLIO = ".Portfolio";
    public static final long TEST_PORTFOLIO_ID = 108L;
    public static final long NOT_EXISTING_LEARNING_OBJECT_ID = 99999L;

    @Test
    public void adding_tag_to_learning_object_adds_a_tag() {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(TEST_PORTFOLIO_ID);
        assertFalse("Tag name", portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG)));

        Response response = doPut(format(ADD_TAG_URL, (Long) TEST_PORTFOLIO_ID), entity(tag(TEST_TAG)));
        assertEquals("Add regular tag", Status.OK.getStatusCode(), response.getStatus());

        Portfolio portfolioAfter = getPortfolio(TEST_PORTFOLIO_ID);
        assertTrue("Tag name", portfolioAfter.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG)));
    }

    @Test
    public void cannot_add_a_tag_to_learning_object_what_does_not_exist() {
        login(USER_PEETER);
        Response response = doPut(format(ADD_TAG_URL, (Long) NOT_EXISTING_LEARNING_OBJECT_ID), entity(tag(TEST_TAG)));
        assertEquals("Add regular tag", Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void adding_system_tag_adds_a_tag() throws Exception {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(TEST_PORTFOLIO_ID);
        assertFalse("System tag name", portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_SYSTEM_TAG)));

        String query = format(ADD_SYSTEM_TAG_URL, TEST_PORTFOLIO_ID, TYPE_PORTFOLIO, TEST_SYSTEM_TAG);
        doGet(query);

        Portfolio portfolioAfter = getPortfolio(TEST_PORTFOLIO_ID);
        assertTrue("System tag name", portfolioAfter.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_SYSTEM_TAG)));
    }

    @Test
    public void adding_tag_with_same_name_throws_an_error() throws Exception {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(TEST_PORTFOLIO_ID);
        assertFalse("Tag  name", portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG_2)));

        Response response = doPut(format(ADD_TAG_URL, (Long) TEST_PORTFOLIO_ID), entity(tag(TEST_TAG_2)));
        assertEquals("Add regular tag", Status.OK.getStatusCode(), response.getStatus());

        Response response2 = doPut(format(ADD_TAG_URL, (Long) TEST_PORTFOLIO_ID), entity(tag(TEST_TAG_2)));
        assertEquals("Add tag with same name", Status.INTERNAL_SERVER_ERROR.getStatusCode(), response2.getStatus());
    }

    @Test
    public void addUserFavorite_adds_learningObject_to_user_favorites() throws Exception {
        login(USER_PEETER);

        LearningObject learningObject = getPortfolio(TEST_PORTFOLIO_ID);
        Response response = doPost(SET_TO_FAVOURITE_URL, learningObject);
        assertEquals("Set to favourite", Status.OK.getStatusCode(), response.getStatus());

        UserFavorite userFavorite = doGet(format(GET_FAVOURITE_URL, TEST_PORTFOLIO_ID), UserFavorite.class);
        assertNotNull("User favourite exist", userFavorite);

        long favouritesCount = doGet(GET_FAVOURITE_COUNT_URL, Long.class);
        assertEquals("User favourite count", 1, favouritesCount);

        doDelete(format(DELETE_FAVOURITE_URL, TEST_PORTFOLIO_ID));
    }

    @Test
    public void cannot_find_user_favorite_when_it_is_not_set() throws Exception {
        login(USER_PEETER);

        UserFavorite userFavorite = doGet(format(GET_FAVOURITE_URL, TEST_PORTFOLIO_ID), UserFavorite.class);
        assertNull("User favourite doesn't exist", userFavorite);
    }

    @Test
    public void deleting_user_favorite_removes_it_from_user_favorites() throws Exception {
        login(USER_PEETER);

        LearningObject learningObject = getPortfolio(TEST_PORTFOLIO_ID);

        doPost(SET_TO_FAVOURITE_URL, learningObject);
        doGet(format(GET_FAVOURITE_URL, TEST_PORTFOLIO_ID), UserFavorite.class);
        doDelete(format(DELETE_FAVOURITE_URL, TEST_PORTFOLIO_ID));

        SearchResult searchResult = doGet(USERS_FAVOURITE_URL, SearchResult.class);
        assertTrue("User favourites doesn't exist", isEmpty(searchResult.getItems()));
    }
}
