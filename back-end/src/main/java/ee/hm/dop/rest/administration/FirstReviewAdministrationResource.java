package ee.hm.dop.rest.administration;

import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.FirstReviewService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("admin/firstReview")
public class FirstReviewAdministrationResource extends BaseResource {

    @Inject
    private FirstReviewService firstReviewService;

    @GET
    @Path("/unReviewed")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<FirstReview> getUnReviewed() {
        return firstReviewService.getUnReviewed(getLoggedInUser());
    }

    @GET
    @Path("/unReviewed/count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnReviewedCount() {
        return ok(firstReviewService.getUnReviewedCount(getLoggedInUser()));
    }

    @POST
    @Path("/setReviewed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void setReviewed(LearningObject learningObject) {
        firstReviewService.setReviewed(learningObject, getLoggedInUser());
    }
}
