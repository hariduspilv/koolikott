package ee.hm.dop.rest.administration;

import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.FirstReviewService;
import ee.hm.dop.service.content.LearningObjectAdministrationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("admin/firstReview/")
public class FirstReviewAdminResource extends BaseResource {

    @Inject
    private FirstReviewService firstReviewService;
    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;

    @GET
    @Path("unReviewed")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<FirstReview> getUnReviewed() {
        return firstReviewService.getUnReviewed(getLoggedInUser());
    }

    @GET
    @Path("unReviewed/count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Long getUnReviewedCount() {
        return firstReviewService.getUnReviewedCount(getLoggedInUser()).longValue();
    }

    @POST
    @Path("setReviewed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void setReviewed(LearningObject learningObject) {
        learningObjectAdministrationService.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject, ReviewStatus.ACCEPTED);
    }
}
