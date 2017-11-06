package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.*;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

public class DeletedAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_DELETED = "admin/deleted/";
    private static final String GET_DELETED_COUNT = "admin/deleted/count";
    private static final String PORTFOLIO_ADD_RECOMMENDATION_URL = "portfolio/recommend";
    private static final String PORTFOLIO_REMOVE_RECOMMENDATION_URL = "portfolio/removeRecommendation";

    @Test
    public void admin_can_get_deleted_portfolios() throws Exception {
        login(USER_ADMIN);
        List<AdminLearningObject> deletedPortfolios = doGet(GET_DELETED, new GenericType<List<AdminLearningObject>>() {
        });
        long deletedPortfoliosCount = doGet(GET_DELETED_COUNT, Long.class);

        assertTrue("Portfolios are deleted", deletedPortfolios.stream().allMatch(AdminLearningObject::isDeleted));
        assertEquals("Deleted portfolios list size, deleted portfolios count", deletedPortfolios.size(), deletedPortfoliosCount);
    }

    @Test
    public void admin_can_add_recommendation_to_portfolio() {
        login(USER_ADMIN);

        Recommendation recommendation = doPost(PORTFOLIO_ADD_RECOMMENDATION_URL, getPortfolio(PORTFOLIO_3), Recommendation.class);
        assertNotNull("Recommendation", recommendation);
        Portfolio portfolioAfterRecommend = getPortfolio(PORTFOLIO_3);
        assertNotNull("Portfolio has recommendations", portfolioAfterRecommend.getRecommendation());
    }

    @Test
    public void admin_can_remove_recommendation_from_portfolio() throws Exception {
        login(USER_ADMIN);
        doPost(PORTFOLIO_ADD_RECOMMENDATION_URL, getPortfolio(PORTFOLIO_3));

        Response responseAfterRemove = doPost(PORTFOLIO_REMOVE_RECOMMENDATION_URL, getPortfolio(PORTFOLIO_3));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), responseAfterRemove.getStatus());
        Portfolio portfolioAfterRemoveRecommend = getPortfolio(PORTFOLIO_3);
        assertNull("Portfolio has no recommendations", portfolioAfterRemoveRecommend.getRecommendation());
    }

    @Test
    public void regular_user_do_not_have_access_to_get_deleted_portfolios() throws Exception {
        login(USER_PEETER);
        Response response = doGet(GET_DELETED);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void regular_user_can_not_add_recommendation_to_portfolio() throws Exception {
        login(USER_PEETER);
        Response response = doPost(PORTFOLIO_ADD_RECOMMENDATION_URL, getPortfolio(PORTFOLIO_3));
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void regular_user_can_not_remove_recommendation_from_portfolio() throws Exception {
        login(USER_PEETER);
        Response response = doPost(PORTFOLIO_ADD_RECOMMENDATION_URL, getPortfolio(PORTFOLIO_3));
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void getDeleted_returns_deleted_materials_to_user_admin() throws Exception {
        login(USER_ADMIN);
        List<AdminLearningObject> deletedMaterials = doGet(GET_DELETED, new GenericType<List<AdminLearningObject>>() {
        });
        long deletedMaterialsCount = doGet(GET_DELETED_COUNT, Long.class);

        assertTrue("Materials are deleted", deletedMaterials.stream().allMatch(AdminLearningObject::isDeleted));
        assertEquals("Deleted materials list size, deleted materials count", deletedMaterials.size(), deletedMaterialsCount);
    }

    @Test
    public void regular_user_do_not_have_access_to_get_deleted_materials() throws Exception {
        login(USER_PEETER);
        Response response = doGet(GET_DELETED);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

}
