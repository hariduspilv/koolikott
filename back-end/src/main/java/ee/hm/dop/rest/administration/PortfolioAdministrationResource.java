package ee.hm.dop.rest.administration;

import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.PortfolioAdministrationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("portfolio")
public class PortfolioAdministrationResource extends BaseResource {

    @Inject
    private PortfolioAdministrationService portfolioAdministrationService;

    @POST
    @Path("recommend")
    @RolesAllowed({RoleString.ADMIN})
    public Recommendation recommendPortfolio(Portfolio portfolio) {
        return portfolioAdministrationService.addRecommendation(portfolio, getLoggedInUser());
    }

    @POST
    @Path("removeRecommendation")
    @RolesAllowed({RoleString.ADMIN})
    public void removedPortfolioRecommendation(Portfolio portfolio) {
        portfolioAdministrationService.removeRecommendation(portfolio, getLoggedInUser());
    }
}
