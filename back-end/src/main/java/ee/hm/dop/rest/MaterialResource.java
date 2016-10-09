package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
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

import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.service.LearningObjectService;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.UserService;

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
    @Path("getAllBySource")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Material> getAllMaterialsByUrl(@QueryParam("source") @Encoded String materialSource)
            throws UnsupportedEncodingException {
        materialSource = URLDecoder.decode(materialSource, "UTF-8");
        return materialService.getBySource(materialSource, true);
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
    public List<Material> getByCreator(@QueryParam("username") String username) {
        if (isBlank(username)) {
            throwBadRequestException("Username parameter is mandatory");
        }

        User creator = userService.getUserByUsername(username);
        if (creator == null) {
            return null;
        }

        return materialService.getByCreator(creator);
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
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public BrokenContent setBrokenMaterial(Material material) {
        return materialService.addBrokenMaterial(material, getLoggedInUser());
    }

    @GET
    @Path("getBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public List<BrokenContent> getBrokenMaterial() {
        return materialService.getBrokenMaterials();
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
    @RolesAllowed({"USER", "ADMIN", "RESTRICTED", "MODERATOR"})
    public Boolean hasSetBroken(@QueryParam("materialId") long materialId) {
        return materialService.hasSetBroken(materialId, getLoggedInUser());
    }

    @GET
    @Path("getDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public List<Material> getDeletedMaterials() {
        return materialService.getDeletedMaterials();
    }
}
