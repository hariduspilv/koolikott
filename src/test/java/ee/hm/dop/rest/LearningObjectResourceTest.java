package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;

public class LearningObjectResourceTest extends ResourceIntegrationTestBase {

    private static final String ADD_TAG_URL = "learningObjects/%s/tags";
    public static final String LEARNING_OBJECTS_GET_NEWEST = "learningObjects/getNewest?maxResults=";

    @Test
    public void addTag() {
        login("38011550077");

        Long id = 108L;
        Tag tag = new Tag();
        tag.setName("timshel");

        Response response = doPut(format(ADD_TAG_URL, id), Entity.entity(tag, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void addTagNoLearningObject() {
        login("38011550077");

        Long id = 99999L;
        Tag tag = new Tag();
        tag.setName("timshel");

        Response response = doPut(format(ADD_TAG_URL, id), Entity.entity(tag, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getNewest() {
        Response response = doGet(LEARNING_OBJECTS_GET_NEWEST + "8");
        List<LearningObject> learningObjects = response.readEntity(new GenericType<List<LearningObject>>() {
        });

        assertNotNull(learningObjects);
        assertEquals(8, learningObjects.size());
        validateNewestAreFirst(learningObjects);
    }

    @Test
    public void getNewest20() {
        Response response = doGet(LEARNING_OBJECTS_GET_NEWEST + "20");
        List<LearningObject> learningObjects = response.readEntity(new GenericType<List<LearningObject>>() {
        });

        assertNotNull(learningObjects);
        assertEquals(20, learningObjects.size());
        validateNewestAreFirst(learningObjects);
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
}
