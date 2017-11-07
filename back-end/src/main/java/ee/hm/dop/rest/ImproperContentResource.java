package ee.hm.dop.rest;

import ee.hm.dop.model.ImproperContent;
<<<<<<< HEAD
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.ImproperContentService;
=======
import ee.hm.dop.service.reviewmanagement.ImproperContentService;
>>>>>>> new-develop

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
<<<<<<< HEAD
            return improperContentService.addImproper(improperContent, getLoggedInUser());
=======
            return improperContentService.save(improperContent, getLoggedInUser());
>>>>>>> new-develop
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
