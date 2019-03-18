package ee.hm.dop.rest;

import ee.hm.dop.model.UserProfile;
import ee.hm.dop.service.login.UserEmailService;
import ee.hm.dop.service.useractions.UserProfileService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/userProfile")
public class UserProfileResource extends BaseResource {

    @Inject
    private UserProfileService userProfileService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserProfile(UserProfile userProfile) {
        return userProfileService.update(userProfile, getLoggedInUser());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserProfile getUserProfile() {
        return userProfileService.getUserProfile(getLoggedInUser());
    }

}
