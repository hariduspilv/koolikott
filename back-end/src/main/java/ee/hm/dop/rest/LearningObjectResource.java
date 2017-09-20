package ee.hm.dop.rest;

import ee.hm.dop.model.*;
import ee.hm.dop.service.content.dto.TagDTO;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.useractions.UserFavoriteService;
import ee.hm.dop.utils.NumberUtils;

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

@Path("learningObject")
public class LearningObjectResource extends BaseResource {

    @Inject
    private TagService tagService;
    @Inject
    private UserFavoriteService userFavoriteService;

    @PUT
    @Path("{learningObjectId}/tags")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LearningObject addTag(@PathParam("learningObjectId") Long learningObjectId, Tag newTag) {
        return tagService.addRegularTag(learningObjectId, newTag.getName(), getLoggedInUser());
    }

    @GET
    @Path("{learningObjectId}/system_tags")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TagDTO addSystemTag(@PathParam("learningObjectId") Long learningObjectId, @QueryParam("type") String type, @QueryParam("name") String tagName) {
        return tagService.addSystemTag(learningObjectId, tagName, getLoggedInUser());
    }

    @GET
    @Path("favorite")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public UserFavorite hasSetFavorite(@QueryParam("id") Long id) {
        return userFavoriteService.hasFavorited(id, getLoggedInUser());
    }

    @GET
    @Path("usersFavorite")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public SearchResult getUsersFavorites(@QueryParam("start") int start, @QueryParam("maxResults") int maxResults) {
        return userFavoriteService.getUserFavoritesSearchResult(getLoggedInUser(), start, NumberUtils.zvl(maxResults, 12));
    }

    @GET
    @Path("usersFavorite/count")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public Long getUsersFavoritesCount() {
        return userFavoriteService.getUserFavoritesSize(getLoggedInUser());
    }

    @POST
    @Path("favorite")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public UserFavorite favoriteLearningObject(LearningObject learningObject) {
        return userFavoriteService.addUserFavorite(learningObject, getLoggedInUser());
    }

    @DELETE
    @Path("favorite")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void removeUserFavorite(@QueryParam("id") long id) {
        userFavoriteService.removeUserFavorite(id, getLoggedInUser());
    }
}
