package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.UserFavorite;
import org.junit.Test;

public class LearningObjectResourceTest extends ResourceIntegrationTestBase {

    public static final String ADD_TAG_URL = "learningObject/%s/tags";
    public static final String ADD_SYSTEM_TAG_URL = "learningObject/%s/system_tags?type=%s&name=%s";
    public static final String FAVOURITE_URL = "favorite";
    public static final String USERS_FAVOURITE_URL = "usersFavorite";
    public static final String USERS_FAVOURITE_COUNT_URL = "usersFavorite/count";
    public static final String TEST_TAG = "timshel";
    public static final String TEST_TAG_2 = "timshel2";
    public static final String TEST_SYSTEM_TAG = "matemaatika";
    public static final long TEST_PORTFOLIO = 108L;
    public static final long NOT_EXISTING_LEARNING_OBJECT_ID = 99999L;
    public static final String TYPE_PORTFOLIO = ".Portfolio";

    @Test
    public void adding_tag_to_learning_object_adds_a_tag() {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(TEST_PORTFOLIO);
        assertFalse(portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG)));

        Response response = doPut(format(ADD_TAG_URL, (Long) TEST_PORTFOLIO), Entity.entity(tag(TEST_TAG), MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Portfolio portfolioAfter = getPortfolio(TEST_PORTFOLIO);
        assertTrue(portfolioAfter.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG)));
    }

    @Test
    public void cannot_add_a_tag_to_learning_object_what_does_not_exist() {
        login(USER_PEETER);
        Response response = doPut(format(ADD_TAG_URL, (Long) NOT_EXISTING_LEARNING_OBJECT_ID), Entity.entity(tag(TEST_TAG), MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void adding_system_tag_adds_a_tag() throws Exception {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(TEST_PORTFOLIO);
        assertFalse(portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_SYSTEM_TAG)));

        String query = format(ADD_SYSTEM_TAG_URL, TEST_PORTFOLIO, TYPE_PORTFOLIO, TEST_SYSTEM_TAG);
        doGet(query);

        Portfolio portfolioAfter = getPortfolio(TEST_PORTFOLIO);
        assertTrue(portfolioAfter.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_SYSTEM_TAG)));
    }

    @Test
    public void adding_same_tag_second_time_throws_an_error() throws Exception {
        login(USER_PEETER);

        Portfolio portfolio = getPortfolio(TEST_PORTFOLIO);
        assertFalse(portfolio.getTags().stream().map(Tag::getName).anyMatch(name -> name.equals(TEST_TAG_2)));

        Response response = doPut(format(ADD_TAG_URL, (Long) TEST_PORTFOLIO), Entity.entity(tag(TEST_TAG_2), MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Response response2 = doPut(format(ADD_TAG_URL, (Long) TEST_PORTFOLIO), Entity.entity(tag(TEST_TAG_2), MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response2.getStatus());
    }

    private void validateNewestAreFirst(List<LearningObject> learningObjects) {
        LearningObject last = null;
        for (LearningObject learningObject : learningObjects) {
            if (last != null && learningObject != null && last.getAdded() != null && learningObject.getAdded() != null) {
                // Check that the learningObjects are from newest to oldest
                assertTrue(last.getAdded().isAfter(learningObject.getAdded())
                        || last.getAdded().isEqual(learningObject.getAdded()));
            }

            if (learningObject != null) {
                last = learningObject;
                assertNotNull(learningObject.getAdded());
            }
        }
    }

    private Tag tag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }
}
