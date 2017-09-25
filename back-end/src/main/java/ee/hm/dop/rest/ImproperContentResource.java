package ee.hm.dop.rest;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.ImproperContentService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("impropers")
public class ImproperContentResource extends BaseResource {

    @Inject
    private ImproperContentService improperContentService;

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
    public List<ImproperContent> getImpropers() {
        return improperContentService.getAll(getLoggedInUser());
    }

    @GET
    @Path("{learningObjectId}")
    public List<ImproperContent> getImproperById(@PathParam("learningObjectId") Long learningObjectId) {
        return improperContentService.getImproperContent(learningObjectId, getLoggedInUser());
    }
}
