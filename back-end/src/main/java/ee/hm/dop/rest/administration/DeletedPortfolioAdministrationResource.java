package ee.hm.dop.rest.administration;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.PortfolioAdministrationService;

@Path("admin/deleted/portfolio")
public class DeletedPortfolioAdministrationResource extends BaseResource {

    @Inject
    private PortfolioAdministrationService portfolioAdministrationService;

    @POST
    @Path("restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void restore(Portfolio portfolio) {
        portfolioAdministrationService.restore(portfolio, getLoggedInUser());
    }

    @GET
    @Path("getDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<Portfolio> getDeletedPortfolios() {
        return portfolioAdministrationService.getDeletedPortfolios();
    }

    @GET
    @Path("getDeleted/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Response getDeletedPortfoliosCount() {
        return ok(portfolioAdministrationService.getDeletedPortfoliosCount());
    }
}
