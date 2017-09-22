package ee.hm.dop.rest.administration;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.MaterialAdministrationService;

@Path("admin/brokenContent/")
public class BrokenContentAdminResource extends BaseResource {

    @Inject
    private MaterialAdministrationService materialAdministrationService;

    @GET
    @Path("getBroken")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<BrokenContent> getBrokenMaterial() {
        return materialAdministrationService.getBrokenMaterials();
    }


    @GET
    @Path("getBroken/count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBrokenMaterialCount() {
        return ok(materialAdministrationService.getBrokenMaterialCount());
    }

    @GET
    @Path("isBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Boolean isBroken(@QueryParam("materialId") long materialId) {
        return materialAdministrationService.isBroken(materialId);
    }

    @POST
    @Path("setNotBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN})
    public void setNotBroken(Material material) {
        materialAdministrationService.setMaterialNotBroken(material);
    }
}
