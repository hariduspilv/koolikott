package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.UserTourData;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserTourDataTest extends ResourceIntegrationTestBase {

    private static final String USER_TOUR_URL = "userTourData";

    @Test
    public void not_logged_in_user_can_not_get_tour() throws Exception {
        Response response = doGet(USER_TOUR_URL);
        assertEquals("User not logged in", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void getUserTourData_creates_new_tour_data_for_new_user() throws Exception {
        login(USER_PEETER);
        UserTourData userTourData = doGet(USER_TOUR_URL, UserTourData.class);
        assertNotNull("User tour data exists", userTourData);
    }

    @Test
    public void getUserTourData_return_user_tour_data() throws Exception {
        login(USER_MATI);
        UserTourData userTourData = doGet(USER_TOUR_URL, UserTourData.class);
        assertNotNull("User tour data exists", userTourData);
    }

    @Test
    public void not_logged_in_user_can_not_addUserTourData() throws Exception {
        UserTourData userTourData = new UserTourData();
        Response response = doPut(USER_TOUR_URL, userTourData);
        assertEquals("User not logged in", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void user_can_not_addUserTourData_to_another_user() throws Exception {
        login(USER_PEETER);
        UserTourData userTourData = doGet(USER_TOUR_URL, UserTourData.class);
        logout();
        login(USER_MATI);
        Response response = doPut(USER_TOUR_URL, userTourData);

        assertEquals("Access denied", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void addUserTourData_updates_user_tour_data() throws Exception {
        login(USER_PEETER);
        UserTourData userTourData = doGet(USER_TOUR_URL, UserTourData.class);
        userTourData.setGeneralTour(true);
        UserTourData userTourDataAfter = doPut(USER_TOUR_URL, userTourData, UserTourData.class);

        assertNotNull("User tour data exists", userTourDataAfter);
        assertEquals("UserTourData general tour", userTourData.isGeneralTour(), userTourDataAfter.isGeneralTour());
    }
}
