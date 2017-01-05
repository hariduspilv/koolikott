package ee.hm.dop.rest;

import ee.hm.dop.model.UserTourData;
import ee.hm.dop.service.UserTourDataService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("userTourData")
public class UserTourDataResource extends BaseResource {

    @Inject
    private UserTourDataService userTourDataService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserTourData getUserTourData() {
        return userTourDataService.getUserTourData(getLoggedInUser());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserTourData addUserTourData(UserTourData userTourData) {
        return userTourDataService.addUserTourData(userTourData);
    }
}
