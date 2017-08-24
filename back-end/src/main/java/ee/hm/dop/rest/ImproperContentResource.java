package ee.hm.dop.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.service.ImproperContentService;
import ee.hm.dop.service.LearningObjectService;

@Path("impropers")
public class ImproperContentResource extends BaseResource {

    @Inject
    private ImproperContentService improperContentService;

    @Inject
    private LearningObjectService learningObjectService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ImproperContent setImproper(ImproperContent improperContent) {
        ImproperContent improper = null;

        try {
            improper = improperContentService.addImproper(improperContent, getLoggedInUser());
        } catch (Exception e) {
            throwBadRequestException(e.getMessage());
        }

        return improper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ImproperContent> getImpropers(@QueryParam("learningObject") Long learningObjectId) {
        List<ImproperContent> result = new ArrayList<>();
        User loggedInUser = getLoggedInUser();

        if (learningObjectId != null) {
            LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);

            if (isUserAdmin(loggedInUser)) {
                result.addAll(improperContentService.getByLearningObject(learningObject, loggedInUser));
            } else {
                ImproperContent improper = improperContentService.getByLearningObjectAndCreator(learningObject,
                        loggedInUser, loggedInUser);

                if (improper != null) {
                    result.add(improper);
                }
            }

        } else {
            result.addAll(improperContentService.getAll(loggedInUser));
        }

        return result;
    }

    @GET
    @Path("materials")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public List<ImproperContent> getImproperMaterials() {
        User loggedInUser = getLoggedInUser();
        return improperContentService.getImproperMaterials(loggedInUser);
    }

    @GET
    @Path("portfolios")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public List<ImproperContent> getImproperPortfolios() {
        User loggedInUser = getLoggedInUser();
        return improperContentService.getImproperPortfolios(loggedInUser);
    }

    @GET
    @Path("materials/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public Response getImproperMaterialsCount() {
        User loggedInUser = getLoggedInUser();
        return Response.ok(improperContentService.getImproperMaterialSize(loggedInUser)).build();
    }

    @GET
    @Path("portfolios/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public Response getImproperPortfoliosCount() {
        User loggedInUser = getLoggedInUser();
        return Response.ok(improperContentService.getImproperPortfolioSize(loggedInUser)).build();
    }

    @DELETE
    @RolesAllowed({RoleString.ADMIN})
    public void removeImpropers(@QueryParam("learningObject") Long learningObjectId) {
        if (learningObjectId == null) {
            throwBadRequestException("learningObject query param is required.");
        }

        LearningObject learningObject = learningObjectService.get(learningObjectId, getLoggedInUser());

        if (learningObject == null) {
            throwNotFoundException();
        }

        List<ImproperContent> impropers = improperContentService.getByLearningObject(learningObject, getLoggedInUser());
        improperContentService.deleteAll(impropers, getLoggedInUser());
    }

    @DELETE
    @Path("{improperContentId}")
    @RolesAllowed({RoleString.ADMIN})
    public void removeImproper(@PathParam("improperContentId") long improperContentId) {
        ImproperContent improper = improperContentService.get(improperContentId, getLoggedInUser());

        if (improper == null) {
            throwNotFoundException();
        }

        List<ImproperContent> impropers = new ArrayList<>();
        impropers.add(improper);
        improperContentService.deleteAll(impropers, getLoggedInUser());
    }

    protected boolean isUserAdmin(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.ADMIN;
    }
}
