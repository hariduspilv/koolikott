package ee.hm.dop.rest;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Media;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.MediaService;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("media")
public class MediaResource extends BaseResource {

    @Inject
    private MediaService mediaService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Media get(@QueryParam("id") long mediaId) {
        return mediaService.get(mediaId, getLoggedInUser());
    }

    @POST
    @Path("create")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Media createMaterial(Media media) {
        return mediaService.save(media, getLoggedInUser());
    }

    @POST
    @Path("update")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Media updateMaterial(Media media) {
        return mediaService.update(media, getLoggedInUser());
    }

}
