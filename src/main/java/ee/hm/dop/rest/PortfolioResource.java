package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.Portfolio;
import ee.hm.dop.service.PortfolioService;

@Path("portfolio")
public class PortfolioResource {

    @Inject
    private PortfolioService portfolioService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Portfolio get(@QueryParam("id") long materialId) {
        return portfolioService.get(materialId);
    }
}
