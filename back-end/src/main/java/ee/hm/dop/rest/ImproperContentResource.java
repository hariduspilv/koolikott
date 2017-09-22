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

import com.google.common.collect.Lists;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.ImproperContentService;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.utils.UserUtil;

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
        try {
            return improperContentService.addImproper(improperContent, getLoggedInUser());
        } catch (Exception e) {
            throw badRequest(e.getMessage());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ImproperContent> getImpropers(@QueryParam("learningObject") Long learningObjectId) {
        List<ImproperContent> result = new ArrayList<>();
        User loggedInUser = getLoggedInUser();

        if (learningObjectId != null) {
            LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);

            if (UserUtil.isAdmin(loggedInUser)) {
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
        return improperContentService.getImproperMaterials(getLoggedInUser());
    }

    @GET
    @Path("portfolios")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public List<ImproperContent> getImproperPortfolios() {
        return improperContentService.getImproperPortfolios(getLoggedInUser());
    }

    @GET
    @Path("materials/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public Response getImproperMaterialsCount() {
        return ok(improperContentService.getImproperMaterialSize(getLoggedInUser()));
    }

    @GET
    @Path("portfolios/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public Response getImproperPortfoliosCount() {
        return ok(improperContentService.getImproperPortfolioSize(getLoggedInUser()));
    }
}
