package ee.hm.dop.rest;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.service.reviewmanagement.ImproperContentService;

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
        return improperContentService.save(improperContent, getLoggedInUser());
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
