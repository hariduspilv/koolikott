package ee.hm.dop.rest.administration;

import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
<<<<<<< HEAD
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.FirstReviewService;
=======
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
>>>>>>> new-develop

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
<<<<<<< HEAD
import javax.ws.rs.core.Response;
=======
>>>>>>> new-develop
import java.util.List;

@Path("admin/firstReview/")
public class FirstReviewAdminResource extends BaseResource {

    @Inject
<<<<<<< HEAD
    private FirstReviewService firstReviewService;
=======
    private FirstReviewAdminService firstReviewAdminService;
    @Inject
    private ReviewManager reviewManager;
>>>>>>> new-develop

    @GET
    @Path("unReviewed")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<FirstReview> getUnReviewed() {
<<<<<<< HEAD
        return firstReviewService.getUnReviewed(getLoggedInUser());
=======
        return firstReviewAdminService.getUnReviewed(getLoggedInUser());
>>>>>>> new-develop
    }

    @GET
    @Path("unReviewed/count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Long getUnReviewedCount() {
<<<<<<< HEAD
        return firstReviewService.getUnReviewedCount(getLoggedInUser()).longValue();
=======
        return firstReviewAdminService.getUnReviewedCount(getLoggedInUser()).longValue();
>>>>>>> new-develop
    }

    @POST
    @Path("setReviewed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void setReviewed(LearningObject learningObject) {
<<<<<<< HEAD
        firstReviewService.setReviewed(learningObject, getLoggedInUser());
=======
        reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject, ReviewStatus.ACCEPTED);
>>>>>>> new-develop
    }
}
