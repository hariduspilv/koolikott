package ee.hm.dop.rest.administration;

<<<<<<< HEAD
import com.google.common.collect.Lists;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.ImproperContentService;
import ee.hm.dop.service.content.LearningObjectService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("impropers")
public class ImproperContentAdminResource extends BaseResource {

    @Inject
    private ImproperContentService improperContentService;
    @Inject
    private LearningObjectService learningObjectService;

    @DELETE
=======
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
>>>>>>> new-develop
    @RolesAllowed({RoleString.MODERATOR, RoleString.ADMIN})
    public void removeImpropers(@QueryParam("learningObject") Long learningObjectId) {
        if (learningObjectId == null) {
            throw badRequest("learningObject query param is required.");
        }
<<<<<<< HEAD
        LearningObject learningObject = learningObjectService.get(learningObjectId, getLoggedInUser());
        if (learningObject == null) {
            throw notFound();
        }
        List<ImproperContent> impropers = improperContentService.getByLearningObject(learningObject, getLoggedInUser());
        improperContentService.deleteAll(impropers, getLoggedInUser());
    }

    @DELETE
    @Path("{improperContentId}")
    @RolesAllowed({RoleString.MODERATOR, RoleString.ADMIN})
    public void removeImproper(@PathParam("improperContentId") long improperContentId) {
        ImproperContent improper = improperContentService.get(improperContentId, getLoggedInUser());
        if (improper == null) {
            throw notFound();
        }
        improperContentService.deleteAll(Lists.newArrayList(improper), getLoggedInUser());
=======
        User loggedInUser = getLoggedInUser();
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);
        if (learningObject == null) {
            throw notFound();
        }
        reviewManager.setEverythingReviewedRefreshLO(loggedInUser, learningObject, ReviewStatus.ACCEPTED);
>>>>>>> new-develop
    }
}
