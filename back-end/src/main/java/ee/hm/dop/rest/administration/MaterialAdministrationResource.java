package ee.hm.dop.rest.administration;

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
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.useractions.UserService;

@Path("material")
public class MaterialAdministrationResource extends BaseResource {

    @Inject
    private MaterialService materialService;
    @Inject
    private UserService userService;

    @GET
    @Path("getBroken")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<BrokenContent> getBrokenMaterial() {
        return materialService.getBrokenMaterials();
    }


    @GET
    @Path("getBroken/count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBrokenMaterialCount() {
        return Response.ok(materialService.getBrokenMaterialCount()).build();
    }

    @GET
    @Path("isBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Boolean isBroken(@QueryParam("materialId") long materialId) {
        return materialService.isBroken(materialId);
    }

    @GET
    @Path("getDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<Material> getDeletedMaterials() {
        return materialService.getDeletedMaterials();
    }

    @GET
    @Path("getDeleted/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Response getDeletedMaterialsCount() {
        return Response.ok(materialService.getDeletedMaterialsCount()).build();
    }
}
