package ee.hm.dop.rest.administration;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.ImproperContentAdminService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("admin/improper/portfolio")
public class ImproperPortfolioAdminResource extends BaseResource{

    @Inject
    private ImproperContentAdminService improperContentAdminService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ImproperContent> getImproperPortfolios() {
        return improperContentAdminService.getImproperPortfolios(getLoggedInUser());
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getImproperPortfoliosCount() {
        return improperContentAdminService.getImproperPortfolioSize(getLoggedInUser());
    }
}
