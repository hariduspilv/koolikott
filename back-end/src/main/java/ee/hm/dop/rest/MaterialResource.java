package ee.hm.dop.rest;

import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.UserService;

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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("material")
public class MaterialResource extends BaseResource {

    @Inject
    private MaterialService materialService;

    @Inject
    private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Material get(@QueryParam("materialId") long materialId) {
        return materialService.get(materialId, getLoggedInUser());
    }

    @GET
    @Path("getBySource")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Material> getMaterialsByUrl(@QueryParam("source") @Encoded String materialSource)
            throws UnsupportedEncodingException {
        materialSource = URLDecoder.decode(materialSource, "UTF-8");
        return materialService.getBySource(materialSource, false);
    }

    @GET
    @Path("getOneBySource")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Material getMaterialByUrl(@QueryParam("source") @Encoded String materialSource)
            throws UnsupportedEncodingException {
        materialSource = URLDecoder.decode(materialSource, "UTF-8");
        return materialService.getOneBySource(materialSource, true);
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
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public void likeMaterial(Material material) {
        materialService.addUserLike(material, getLoggedInUser(), true);
    }

    @POST
    @Path("dislike")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public void dislikeMaterial(Material material) {
        materialService.addUserLike(material, getLoggedInUser(), false);
    }

    @POST
    @Path("recommend")
    @RolesAllowed({"ADMIN"})
    public Recommendation recommendMaterial(Material material) {
        return materialService.addRecommendation(material, getLoggedInUser());
    }

    @POST
    @Path("removeRecommendation")
    @RolesAllowed({"ADMIN"})
    public void removedMaterialRecommendation(Material material) {
        materialService.removeRecommendation(material, getLoggedInUser());
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
    @RolesAllowed({"ADMIN"})
    public void delete(@PathParam("materialID") Long materialID) {
        materialService.delete(materialID, getLoggedInUser());
    }

    @POST
    @Path("restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public void restore(Material material) {
        materialService.restore(material, getLoggedInUser());
    }

    @PUT
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Material createOrUpdateMaterial(Material material) {
        Material newMaterial = null;

        if (material.getId() == null) {
            newMaterial = materialService.createMaterial(material, getLoggedInUser(), true);
        } else if (getLoggedInUser() != null) {
            newMaterial = materialService.update(material, getLoggedInUser(), true);
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
    @Path("getBroken")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<BrokenContent> getBrokenMaterial() {
        return materialService.getBrokenMaterials();
    }


    @GET
    @Path("getBroken/count")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBrokenMaterialCount() {
        return Response.ok(materialService.getBrokenMaterialCount()).build();
    }

    @POST
    @Path("setNotBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public void setNotBroken(Material material) {
        materialService.setMaterialNotBroken(material);
    }

    @GET
    @Path("isBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Boolean isBroken(@QueryParam("materialId") long materialId) {
        return materialService.isBroken(materialId);
    }

    @GET
    @Path("hasSetBroken")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean hasSetBroken(@QueryParam("materialId") long materialId) {
        User user = getLoggedInUser();
        if(user != null) {
            return materialService.hasSetBroken(materialId, getLoggedInUser());
        }

        return false;
    }

    @GET
    @Path("getDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public List<Material> getDeletedMaterials() {
        return materialService.getDeletedMaterials();
    }

    @GET
    @Path("getDeleted/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response getDeletedMaterialsCount() {
        return Response.ok(materialService.getDeletedMaterials().size()).build();

    }

    @GET
    @Path("externalMaterial/")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProxyUrl(@QueryParam("url") String url_param) throws IOException {
        return materialService.getProxyUrl(url_param);
    }
}
