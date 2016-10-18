package ee.hm.dop.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.UserFavorite;
import ee.hm.dop.service.LearningObjectService;
import ee.hm.dop.service.TagService;

@Path("learningObject")
public class LearningObjectResource extends BaseResource {

    @Inject
    private LearningObjectService learningObjectService;

    @Inject
    private TagService tagService;

    @PUT
    @Path("{learningObjectId}/tags")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
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
    @Path("favorite")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR", "RESTRICTED"})
    public UserFavorite hasSetFavorite(@QueryParam("id") Long id) {
        return learningObjectService.hasFavorited(id, getLoggedInUser());


    }

    @GET
    @Path("usersFavorite")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR", "RESTRICTED"})
    public List<LearningObject> getUsersFavorites() {
        return learningObjectService.getUserFavorites(getLoggedInUser());
    }

    @Path("usersFavorite/count")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR", "RESTRICTED"})
    public Response getUsersFavoritesCount() {
        return Response.ok(learningObjectService.getUserFavorites(getLoggedInUser()).size()).build();

    }

    @POST
    @Path("favorite")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public UserFavorite favoriteLearningObject(LearningObject learningObject) {
        return learningObjectService.addUserFavorite(learningObject, getLoggedInUser());
    }

    @DELETE
    @Path("favorite")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public void removeUserFavorite(@QueryParam("id") long id) {
        learningObjectService.removeUserFavorite(id, getLoggedInUser());
    }
}
