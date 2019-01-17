package ee.hm.dop.rest.administration;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.ImproperContentAdminService;
import ee.hm.dop.service.reviewmanagement.ImproperContentService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("admin/improper")
public class ImproperContentAdminResource extends BaseResource {

    @Inject
    private ImproperContentAdminService improperContentAdminService;
    @Inject
    private ReviewManager reviewManager;
    @Inject
    private ImproperContentService improperContentService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<AdminLearningObject> getImproper() {
        return improperContentAdminService.getImproper(getLoggedInUser());
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getImproperCount() {
        return improperContentAdminService.getImproperCount(getLoggedInUser());
    }

    @POST
    @Path("setProper")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.MODERATOR, RoleString.ADMIN})
    public LearningObject setProper(LearningObjectMiniDto loDto) {
        return reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), loDto.convert(), ReviewStatus.ACCEPTED, ReviewType.IMPROPER);
    }

    @GET
    @Path("{learningObjectId}")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<ImproperContent> getImproperById(@PathParam("learningObjectId") Long learningObjectId) {
        return improperContentService.getImproperContent(learningObjectId, getLoggedInUser());
    }
}
