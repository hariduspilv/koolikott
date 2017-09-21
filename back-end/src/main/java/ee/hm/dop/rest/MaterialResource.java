package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.MaterialProxy;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.useractions.UserLikeService;
import ee.hm.dop.service.useractions.UserService;

@Path("material")
public class MaterialResource extends BaseResource {

    @Inject
    private MaterialService materialService;
    @Inject
    private UserService userService;
    @Inject
    private MaterialProxy materialProxy;
    @Inject
    private UserLikeService userLikeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Material get(@QueryParam("materialId") long materialId) {
        return materialService.get(materialId, getLoggedInUser());
    }

    @GET
    @Path("getBySource")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Material> getMaterialsByUrl(@QueryParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        materialSource = URLDecoder.decode(materialSource, "UTF-8");
        return materialService.getBySource(materialSource, GetMaterialStrategy.ONLY_EXISTING);
    }

    @GET
    @Path("getOneBySource")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Material getMaterialByUrl(@QueryParam("source") @Encoded String materialSource)
            throws UnsupportedEncodingException {
        materialSource = URLDecoder.decode(materialSource, "UTF-8");
        return materialService.getOneBySource(materialSource, GetMaterialStrategy.INCLUDE_DELETED);
    }

    @POST
    @Path("increaseViewCount")
    public Response increaseViewCount(Material material) {
        Long materialId = material.getId();

        Material originalMaterial = materialService.get(materialId, getLoggedInUser());
        if (originalMaterial == null) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("Invalid material").build();
        }

        materialService.increaseViewCount(originalMaterial);
        return Response.status(HttpURLConnection.HTTP_OK).build();
    }

    @POST
    @Path("like")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void likeMaterial(Material material) {
        userLikeService.addUserLike(material, getLoggedInUser(), true);
    }

    @POST
    @Path("dislike")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void dislikeMaterial(Material material) {
        userLikeService.addUserLike(material, getLoggedInUser(), false);
    }

    @POST
    @Path("getUserLike")
    public UserLike getUserLike(Material material) {
        return materialService.getUserLike(material, getLoggedInUser());
    }

    @POST
    @Path("removeUserLike")
    public void removeUserLike(Material material) {
        materialService.removeUserLike(material, getLoggedInUser());
    }

    @GET
    @Path("getByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult getByCreator(@QueryParam("username") String username, @QueryParam("start") int start, @QueryParam("maxResults") int maxResults) {
        if (maxResults == 0) maxResults = 12;
        if (isBlank(username)) throwBadRequestException("Username parameter is mandatory");

        User creator = userService.getUserByUsername(username);
        if (creator == null) {
            return null;
        }

        List<Searchable> userFavorites = new ArrayList<>(materialService.getByCreator(creator, start, maxResults));
        return new SearchResult(userFavorites, materialService.getByCreatorSize(creator), start);

    }

    @GET
    @Path("getByCreator/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Long getByCreatorCount(@QueryParam("username") String username) {
        if (isBlank(username)) throwBadRequestException("Username parameter is mandatory");
        User creator = userService.getUserByUsername(username);
        if (creator == null) return null;

        return materialService.getByCreatorSize(creator);

    }

    @DELETE
    @Path("{materialID}")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void delete(@PathParam("materialID") Long materialID) {
        materialService.delete(materialID, getLoggedInUser());
    }

    @POST
    @Path("restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN})
    public void restore(Material material) {
        materialService.restore(material, getLoggedInUser());
    }

    @PUT
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Material createOrUpdateMaterial(Material material) {
        Material newMaterial = null;

        if (material.getId() == null) {
            newMaterial = materialService.createMaterial(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
        } else if (getLoggedInUser() != null) {
            newMaterial = materialService.update(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
        } else {
            throwBadRequestException("Unable to add or update material - can extract get logged in user.");
        }

        return newMaterial;
    }

    @POST
    @Path("setBroken")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BrokenContent setBrokenMaterial(Material material) {
        return materialService.addBrokenMaterial(material, getLoggedInUser());
    }

    @GET
    @Path("hasSetBroken")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean hasSetBroken(@QueryParam("materialId") long materialId) {
        User user = getLoggedInUser();
        if (user != null) {
            return materialService.hasSetBroken(materialId, getLoggedInUser());
        }
        return false;
    }

    @GET
    @Path("externalMaterial/")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProxyUrl(@QueryParam("url") String url_param) throws IOException {
        return materialProxy.getProxyUrl(url_param);
    }
}
