package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.HttpURLConnection;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.User;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.UserService;

@Path("material")
public class MaterialResource {

    @Inject
    private MaterialService materialService;

    @Inject
    private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Material get(@QueryParam("materialId") long materialId) {
        return materialService.get(materialId);
    }

    @GET
    @Path("getNewestMaterials")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Material> getNewestMaterials(@QueryParam("numberOfMaterials") int numberOfMaterials) {
        return materialService.getNewestMaterials(numberOfMaterials);
    }

    @POST
    @Path("increaseViewCount")
    public Response increaseViewCount(Material materialWithOnlyId) {
        Long materialId = materialWithOnlyId.getId();

        Material material = materialService.get(materialId);
        if (material == null) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("Invalid material").build();
        }

        materialService.increaseViewCount(material);
        return Response.status(HttpURLConnection.HTTP_OK).build();
    }

    @GET
    @Path("/getPicture")
    @Produces("image/png")
    public Response getPictureById(@QueryParam("materialId") long id) {
        Material material = new Material();
        material.setId(id);
        byte[] pictureData = materialService.getMaterialPicture(material);

        if (pictureData != null) {
            return Response.ok(pictureData).build();
        } else {
            return Response.status(HttpURLConnection.HTTP_NOT_FOUND).build();
        }
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

    private void throwBadRequestException(String message) {
        throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(message).build());
    }
}
