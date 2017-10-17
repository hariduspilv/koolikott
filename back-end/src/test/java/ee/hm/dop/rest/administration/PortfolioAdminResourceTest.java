package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

public class PortfolioAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_DELETED_PORTFOLIOS = "admin/deleted/portfolio/getDeleted";
    private static final String GET_DELETED_PORTFOLIOS_COUNT = "admin/deleted/portfolio/getDeleted/count";
    private static final String PORTFOLIO_ADD_RECOMMENDATION_URL = "portfolio/recommend";
    private static final String PORTFOLIO_REMOVE_RECOMMENDATION_URL = "portfolio/removeRecommendation";

    @Test
    public void admin_can_get_deleted_portfolios() throws Exception {
        login(USER_ADMIN);
        List<Portfolio> deletedPortfolios = doGet(GET_DELETED_PORTFOLIOS, new GenericType<List<Portfolio>>() {
        });
        long deletedPortfoliosCount = doGet(GET_DELETED_PORTFOLIOS_COUNT, Long.class);

        assertTrue("Portfolios are deleted", deletedPortfolios.stream().allMatch(LearningObject::isDeleted));
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
        Response response = doGet(GET_DELETED_PORTFOLIOS);
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
}
