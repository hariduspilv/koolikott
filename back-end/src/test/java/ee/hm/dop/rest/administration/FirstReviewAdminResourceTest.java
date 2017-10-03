package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class FirstReviewAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_UNREVIEWED = "admin/firstReview/unReviewed";
    private static final String GET_UNREVIEWED_COUNT = "admin/firstReview/unReviewed/count";
    private static final String SET_REVIEWED = "admin/firstReview/setReviewed";
    private static final long PRIVATE_PORTFOLIO = TestConstants.PORTFOLIO_7;

    @Test
    public void after_first_review_is_reviewed_it_is_not_returned_by_getUnreviewed() {
        login(TestConstants.USER_ADMIN);

        List<FirstReview> firstReviews = doGet(GET_UNREVIEWED, listType());
        BigDecimal count = doGet(GET_UNREVIEWED_COUNT, BigDecimal.class);
        assertEquals("UnReviewed size, UnReviewed count", firstReviews.size(), count.longValueExact());

        LearningObject learningObject = firstReviews.stream()
                .map(FirstReview::getLearningObject)
                .filter(l -> l.getId().equals(TestConstants.MATERIAL_1))
                .findAny()
                .orElseThrow(RuntimeException::new);
        Long learningObjectId = learningObject.getId();
        Response updateResponse = doPost(SET_REVIEWED, learningObject);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), updateResponse.getStatus());

        List<FirstReview> firstReviews2 = doGet(GET_UNREVIEWED, listType());

        boolean noneMatchUpdatedOne = firstReviews2.stream().map(FirstReview::getLearningObject).map(LearningObject::getId)
                .noneMatch(l -> l.equals(learningObjectId));
        assertTrue(noneMatchUpdatedOne);

        BigDecimal count2 = doGet(GET_UNREVIEWED_COUNT, BigDecimal.class);
        assertEquals("UnReviewed size, UnReviewed count", firstReviews2.size(), count2.longValueExact());
    }

    @Test
    public void getUnreviewed_returns_not_an_empty_list() {
        login(TestConstants.USER_ADMIN);

        List<FirstReview> firstReviews = doGet(GET_UNREVIEWED, listType());
        assertTrue("UnReviewed list", CollectionUtils.isNotEmpty(firstReviews));
    }

    @Test
    public void private_portfolio_is_not_returned_as_part_of_the_unreviewed() {
        login(TestConstants.USER_ADMIN);

        List<FirstReview> firstReviews = doGet(GET_UNREVIEWED, listType());

        boolean noneMatchUpdatedOne = firstReviews.stream().map(FirstReview::getLearningObject).map(LearningObject::getId)
                .noneMatch(l -> l.equals(PRIVATE_PORTFOLIO));
        assertTrue(noneMatchUpdatedOne);
        assertTrue("UnReviewed list", CollectionUtils.isNotEmpty(firstReviews));
    }

    @Test
    public void getUnreviewed_returns_different_unReviewed_materials_based_on_user_priviledge() {
        login(TestConstants.USER_MODERATOR);
        List<FirstReview> firstReviewsModerator = doGet(GET_UNREVIEWED, listType());

        login(TestConstants.USER_ADMIN);
        List<FirstReview> firstReviewsAdmin = doGet(GET_UNREVIEWED, listType());

        assertNotEquals("Admin UnReviewed list, Moderator UnReviewed list", firstReviewsAdmin, firstReviewsModerator);
    }

    @Test
    public void unreviewed_learningObject_is_unreviewed() throws Exception {
        login(TestConstants.USER_ADMIN);
        Material material = getMaterial(TestConstants.MATERIAL_2);
        assertEquals("Is reviewed", 1, material.getUnReviewed());
    }

    @Test
    public void unreviewed_learningObject_after_being_set_reviewed_is_reviewed() throws Exception {
        login(TestConstants.USER_ADMIN);
        Material material = getMaterial(TestConstants.MATERIAL_2);
        assertEquals("Is Reviewed",1, material.getUnReviewed());

        Response updateResponse = doPost(SET_REVIEWED, material);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), updateResponse.getStatus());

        Material materialAfter = getMaterial(TestConstants.MATERIAL_2);
        assertEquals("Is Reviewed",0, materialAfter.getUnReviewed());
    }

    @Test
    public void unAuthorized_user_can_not_view() throws Exception {
        login(TestConstants.USER_MATI);
        List<FirstReview> firstReviews = doGet(GET_UNREVIEWED, listType());
        assertNull("UnReviewed list", firstReviews);
    }

    private GenericType<List<FirstReview>> listType() {
        return new GenericType<List<FirstReview>>() {
        };
    }
}
