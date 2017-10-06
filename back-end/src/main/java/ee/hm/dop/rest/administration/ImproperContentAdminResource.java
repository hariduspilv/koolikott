package ee.hm.dop.rest.administration;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.ImproperContentAdminService;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.content.LearningObjectService;
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
    private LearningObjectService learningObjectService;
    @Inject
    private ReviewManager reviewManager;

    @GET
    @Path("material")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ImproperContent> getImproperMaterials() {
        return improperContentAdminService.getImproperMaterials(getLoggedInUser());
    }

    @GET
    @Path("material/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getImproperMaterialsCount() {
        return improperContentAdminService.getImproperMaterialSize(getLoggedInUser());
    }

    @GET
    @Path("portfolio")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ImproperContent> getImproperPortfolios() {
        return improperContentAdminService.getImproperPortfolios(getLoggedInUser());
    }

    @GET
    @Path("portfolio/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getImproperPortfoliosCount() {
        return improperContentAdminService.getImproperPortfolioSize(getLoggedInUser());
    }

    @DELETE
    @Path("setProper")
    @RolesAllowed({RoleString.MODERATOR, RoleString.ADMIN})
    public void removeImpropers(@QueryParam("learningObject") Long learningObjectId) {
        if (learningObjectId == null) {
            throw badRequest("learningObject query param is required.");
        }
        User loggedInUser = getLoggedInUser();
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);
        if (learningObject == null) {
            throw notFound();
        }
        reviewManager.setEverythingReviewedRefreshLO(loggedInUser, learningObject, ReviewStatus.ACCEPTED);
    }
}
