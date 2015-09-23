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

import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.service.PortfolioService;
import ee.hm.dop.service.UserService;

@Path("portfolio")
public class PortfolioResource {

    @Inject
    private PortfolioService portfolioService;

    @Inject
    private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Portfolio get(@QueryParam("id") long portfolioId) {
        return portfolioService.get(portfolioId);
    }

    @GET
    @Path("getByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Portfolio> getByCreator(@QueryParam("username") String username) {
        if (isBlank(username)) {
            throwBadRequestException("Username parameter is mandatory");
        }

        User creator = userService.getUserByUsername(username);
        if (creator == null) {
            throwBadRequestException("Invalid request");
        }

        return portfolioService.getByCreator(creator);
    }

    @GET
    @Path("/getPicture")
    @Produces("image/png")
    public Response getPictureById(@QueryParam("portfolioId") long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);
        byte[] pictureData = portfolioService.getPortfolioPicture(portfolio);

        if (pictureData != null) {
            return Response.ok(pictureData).build();
        } else {
            return Response.status(HttpURLConnection.HTTP_NOT_FOUND).build();
        }
    }

    private void throwBadRequestException(String message) {
        throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(message).build());
    }

    @POST
    @Path("increaseViewCount")
    public void increaseViewCount(Portfolio portfolio) {
        portfolioService.incrementViewCount(portfolio);
    }
}
