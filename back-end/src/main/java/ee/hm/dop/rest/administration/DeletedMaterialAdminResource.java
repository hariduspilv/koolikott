package ee.hm.dop.rest.administration;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.MaterialAdministrationService;
import ee.hm.dop.service.content.MaterialService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("admin/deleted/material")
public class DeletedMaterialAdminResource extends BaseResource {

    @Inject
    private MaterialAdministrationService materialAdministrationService;
    @Inject
    private MaterialService materialService;

    @POST
    @Path("restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN})
    public void restore(Material material) {
        materialService.restore(material, getLoggedInUser());
    }

    @GET
    @Path("getDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<Material> getDeletedMaterials() {
        return materialAdministrationService.getDeletedMaterials();
    }

    @GET
    @Path("getDeleted/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getDeletedMaterialsCount() {
        return materialAdministrationService.getDeletedMaterialsCount();
    }
}
