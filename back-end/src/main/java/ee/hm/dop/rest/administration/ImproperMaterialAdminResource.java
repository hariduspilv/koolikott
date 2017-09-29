package ee.hm.dop.rest.administration;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.ImproperContentAdminService;
import ee.hm.dop.service.content.ImproperContentService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("admin/improper/material")
public class ImproperMaterialAdminResource extends BaseResource {

    @Inject
    private ImproperContentAdminService improperContentAdminService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ImproperContent> getImproperMaterials() {
        return improperContentAdminService.getImproperMaterials(getLoggedInUser());
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getImproperMaterialsCount() {
        return improperContentAdminService.getImproperMaterialSize(getLoggedInUser());
    }

}
