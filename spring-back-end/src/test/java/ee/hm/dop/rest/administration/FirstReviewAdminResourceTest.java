package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FirstReviewAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_UNREVIEWED = "admin/firstReview/unReviewed?itemSortedBy=byCreatedAt";
    private static final String GET_UNREVIEWED_COUNT = "admin/firstReview/unReviewed/count";
    private static final String SET_REVIEWED = "admin/firstReview/setReviewed";
    private static final long PRIVATE_PORTFOLIO = PORTFOLIO_7;

    @Ignore
    //todo transactions
    @Test
    public void after_first_review_is_reviewed_it_is_not_returned_by_getUnreviewed() {
        login(USER_ADMIN);

        List<AdminLearningObject> firstReviews = doGet(GET_UNREVIEWED, listTypeAdminLO());
        BigDecimal count = doGet(GET_UNREVIEWED_COUNT, BigDecimal.class);
        assertEquals("UnReviewed size, UnReviewed count", firstReviews.size(), count.longValueExact());

        Optional<AdminLearningObject> firstReview = firstReviews.stream()
                .filter(l -> l.getId().equals(MATERIAL_21))
                .findAny();
        assertTrue(firstReview.isPresent());
        AdminLearningObject learningObject = firstReview.orElseThrow(RuntimeException::new);
        Long learningObjectId = learningObject.getId();
        Material updatedMaterial = doPost(SET_REVIEWED, materialWithId(MATERIAL_21), Material.class);
        assertNotNull(updatedMaterial);
        assertTrue(updatedMaterial.getUnReviewed() == 0);

        List<AdminLearningObject> firstReviews2 = doGet(GET_UNREVIEWED, listTypeAdminLO());

        boolean noneMatchUpdatedOne = firstReviews2.stream().map(AdminLearningObject::getId)
                .noneMatch(l -> l.equals(learningObjectId));
        assertTrue(noneMatchUpdatedOne);

        BigDecimal count2 = doGet(GET_UNREVIEWED_COUNT, BigDecimal.class);
        assertEquals("UnReviewed size, UnReviewed count", firstReviews2.size(), count2.longValueExact());
    }

    @Ignore
    @Test
    public void getUnreviewed_returns_not_an_empty_list() {
        login(USER_ADMIN);

        List<AdminLearningObject> firstReviews = doGet(GET_UNREVIEWED, listTypeAdminLO());
        assertTrue("UnReviewed list", CollectionUtils.isNotEmpty(firstReviews));
    }

    @Ignore
    @Test
    public void private_portfolio_is_not_returned_as_part_of_the_unreviewed() {
        login(USER_ADMIN);

        List<AdminLearningObject> firstReviews = doGet(GET_UNREVIEWED, listTypeAdminLO());

        boolean noneMatchUpdatedOne = firstReviews.stream().map(AdminLearningObject::getId)
                .noneMatch(l -> l.equals(PRIVATE_PORTFOLIO));
        assertTrue(noneMatchUpdatedOne);
        assertTrue("UnReviewed list", CollectionUtils.isNotEmpty(firstReviews));
    }

    @Ignore
    @Test
    public void getUnreviewed_returns_different_unReviewed_materials_based_on_user_priviledge() {
        login(USER_MODERATOR);
        SearchResult firstReviewsModerator = doGet(GET_UNREVIEWED, searchResult());
        assertTrue(CollectionUtils.isNotEmpty(firstReviewsModerator.getItems()));
        logout();

        login(USER_ADMIN);
        SearchResult firstReviewsAdmin = doGet(GET_UNREVIEWED, searchResult());
        assertTrue(CollectionUtils.isNotEmpty(firstReviewsAdmin.getItems()));

        assertNotEquals("Admin UnReviewed list, Moderator UnReviewed list", firstReviewsAdmin, firstReviewsModerator);
    }

    @Test
    public void unreviewed_learningObject_is_unreviewed()  {
        login(USER_ADMIN);
        Material material = getMaterial(MATERIAL_2);
        assertEquals("Is reviewed", 1, material.getUnReviewed());
    }

    @Ignore
    //todo transaction
    @Test
    public void unreviewed_learningObject_after_being_set_reviewed_is_reviewed()  {
        login(USER_ADMIN);
        Material material = getMaterial(MATERIAL_15);
        assertEquals("Is Reviewed",1, material.getUnReviewed());

        Material materialAfter = doPost(SET_REVIEWED, material, Material.class);
        assertEquals("Is Reviewed",0, materialAfter.getUnReviewed());
    }

    @Test
    public void unAuthorized_user_can_not_view()  {
        login(USER_MATI);
        Response response = doGet(GET_UNREVIEWED);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    private GenericType<List<AdminLearningObject>> listTypeAdminLO() {
        return new GenericType<List<AdminLearningObject>>() {
        };
    }

    private GenericType<SearchResult> searchResult() {
        return new GenericType<SearchResult>() {
        };
    }

}
