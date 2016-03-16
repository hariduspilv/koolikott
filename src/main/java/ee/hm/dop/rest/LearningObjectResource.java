package ee.hm.dop.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.service.LearningObjectService;
import ee.hm.dop.service.TagService;

@Path("learningObjects")
public class LearningObjectResource extends BaseResource {

    @Inject
    private LearningObjectService learningObjectService;

    @Inject
    private TagService tagService;

    @PUT
    @Path("{learningObjectId}/tags")
    @RolesAllowed({ "USER", "ADMIN" })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LearningObject addTag(@PathParam("learningObjectId") Long learningObjectId, Tag newTag) {
        LearningObject learningObject = learningObjectService.get(learningObjectId, getLoggedInUser());
        if (learningObject == null) {
            throwNotFoundException("No such learning object");
        }

        Tag tag = tagService.getTagByName(newTag.getName());
        if (tag == null) {
            tag = newTag;
        }

        return learningObjectService.addTag(learningObject, tag, getLoggedInUser());
    }

    @GET
    @Path("getNewest")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LearningObject> getNewestLearningObjects(@QueryParam("maxResults") int numberOfLearningObjects) {
        return learningObjectService.getNewestLearningObjects(numberOfLearningObjects);
    }

    @GET
    @Path("getPopular")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LearningObject> getPopularLearningObjects(@QueryParam("maxResults") int numberOfLearningObjects) {
        return learningObjectService.getPopularLearningObjects(numberOfLearningObjects);
    }
}
