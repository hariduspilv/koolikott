package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FirstReviewAdministrationResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_UNREVIEWED = "admin/firstReview/unReviewed";
    private static final String GET_UNREVIEWED_COUNT = "admin/firstReview/unReviewed/count";
    private static final String SET_REVIEWED = "admin/firstReview/setReviewed";
    public static final long PRIVATE_PORTFOLIO = 107L;

    @Test
    public void after_first_review_is_reviewed_it_is_not_returned_by_getUnreviewed() {
        login("89898989898");

        Response initialResponse = doGet(GET_UNREVIEWED, MediaType.APPLICATION_JSON_TYPE);
        List<FirstReview> firstReviews = initialResponse.readEntity(new GenericType<List<FirstReview>>() {
        });
        Response initialCount = doGet(GET_UNREVIEWED_COUNT, MediaType.APPLICATION_JSON_TYPE);
        BigDecimal count = initialCount.readEntity(BigDecimal.class);
        assertEquals(firstReviews.size(), count.longValueExact());

        LearningObject learningObject = firstReviews.get(0).getLearningObject();
        Long learningObjectId = learningObject.getId();
        Response updateResponse = doPost(SET_REVIEWED, Entity.entity(learningObject, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), updateResponse.getStatus());


        Response afterUpdateResponse = doGet(GET_UNREVIEWED, MediaType.APPLICATION_JSON_TYPE);
        List<FirstReview> firstReviews2 = afterUpdateResponse.readEntity(new GenericType<List<FirstReview>>() {
        });

        boolean noneMatchUpdatedOne = firstReviews2.stream().map(FirstReview::getLearningObject).map(LearningObject::getId)
                .noneMatch(l -> l.equals(learningObjectId));
        assertTrue(noneMatchUpdatedOne);

        Response afterUpdateCount = doGet(GET_UNREVIEWED_COUNT, MediaType.APPLICATION_JSON_TYPE);
        BigDecimal count2 = afterUpdateCount.readEntity(BigDecimal.class);
        assertEquals(firstReviews2.size(), count2.longValueExact());
    }

    @Test
    public void getUnreviewed_returns_not_an_empty_list() {
        login("89898989898");

        Response response = doGet(GET_UNREVIEWED, MediaType.APPLICATION_JSON_TYPE);
        List<FirstReview> firstReviews = response.readEntity(new GenericType<List<FirstReview>>() {
        });
        assertTrue(CollectionUtils.isNotEmpty(firstReviews));
    }

    @Test
    public void private_portfolio_is_not_returned_as_part_of_the_unreviewed() {
        login("89898989898");

        Response response = doGet(GET_UNREVIEWED, MediaType.APPLICATION_JSON_TYPE);
        List<FirstReview> firstReviews = response.readEntity(new GenericType<List<FirstReview>>() {
        });

        boolean noneMatchUpdatedOne = firstReviews.stream().map(FirstReview::getLearningObject).map(LearningObject::getId)
                .noneMatch(l -> l.equals(PRIVATE_PORTFOLIO));
        assertTrue(noneMatchUpdatedOne);
        assertTrue(CollectionUtils.isNotEmpty(firstReviews));
    }
}
