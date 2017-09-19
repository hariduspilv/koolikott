package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class FirstReviewResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_FIRST_REVIEW_URL = "firstReview";
    private static final String SET_REVIEWED = "firstReview/setReviewed";

    @Test
    public void getUnReviewed() {
        login("89898989898");

        Response response = doGet(GET_FIRST_REVIEW_URL, MediaType.APPLICATION_JSON_TYPE);
        List<FirstReview> firstReviews = response.readEntity(new GenericType<List<FirstReview>>() {
        });

    }

    @Test
    public void setReviewed() {
        login("89898989899");
        long firstReviewId = 5;

        FirstReview firstReview = getFirstReview(firstReviewId);

        Response response = doPost(SET_REVIEWED, Entity.entity(firstReview, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    private FirstReview getFirstReview(long firstReviewId) {
        return doGet(format(GET_FIRST_REVIEW_URL, firstReviewId), FirstReview.class);
    }
}
