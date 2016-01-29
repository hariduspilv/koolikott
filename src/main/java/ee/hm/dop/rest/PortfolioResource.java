package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.HttpURLConnection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.service.PortfolioService;
import ee.hm.dop.service.UserService;

@Path("portfolio")
public class PortfolioResource extends BaseResource {

    @Inject
    private PortfolioService portfolioService;

    @Inject
    private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Portfolio get(@QueryParam("id") long portfolioId) {
        User loggedInUser = getLoggedInUser();

        return portfolioService.get(portfolioId, loggedInUser);
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

        User loggedInUser = getLoggedInUser();

        return portfolioService.getByCreator(creator, loggedInUser);
    }

    @GET
    @Path("/getPicture")
    @Produces("image/png")
    public Response getPictureById(@QueryParam("portfolioId") long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);
        User loggedInUser = getLoggedInUser();
        String pictureData = portfolioService.getPortfolioPicture(portfolio, loggedInUser);

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

    @POST
    @Path("like")
    public void likePortfolio(Portfolio portfolio) {
        portfolioService.addUserLike(portfolio, getLoggedInUser(), true);
    }

    @POST
    @Path("dislike")
    public void dislikePortfolio(Portfolio portfolio) {
        portfolioService.addUserLike(portfolio, getLoggedInUser(), false);
    }

    @POST
    @Path("recommend")
    @RolesAllowed({ "ADMIN" })
    public Recommendation recommendPortfolio(Portfolio portfolio) {
        return portfolioService.addRecommendation(portfolio, getLoggedInUser());
    }

    @POST
    @Path("removeRecommendation")
    @RolesAllowed({ "ADMIN" })
    public void removedPortfolioRecommendation(Portfolio portfolio) {
        portfolioService.removeRecommendation(portfolio, getLoggedInUser());
    }

    @POST
    @Path("getUserLike")
    public UserLike getUserLike(Portfolio portfolio) {
        return portfolioService.getUserLike(portfolio, getLoggedInUser());
    }

    @POST
    @Path("removeUserLike")
    public void removeUserLike(Portfolio portfolio) {
        portfolioService.removeUserLike(portfolio, getLoggedInUser());
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "USER", "ADMIN", "PUBLISHER" })
    public Portfolio create(Portfolio portfolio) {
        return portfolioService.create(portfolio, getLoggedInUser());
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "USER", "ADMIN", "PUBLISHER" })
    public Portfolio update(Portfolio portfolio) {
        return portfolioService.update(portfolio, getLoggedInUser());
    }

    @POST
    @Path("copy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "USER", "ADMIN", "PUBLISHER" })
    public Portfolio copy(Portfolio portfolio) {
        return portfolioService.copy(portfolio, getLoggedInUser());
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "USER", "ADMIN", "PUBLISHER" })
    public void delete(Portfolio portfolio) {
        portfolioService.delete(portfolio, getLoggedInUser());
    }

    @POST
    @Path("restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "ADMIN" })
    public void restore(Portfolio portfolio) {
        portfolioService.restore(portfolio, getLoggedInUser());
    }

    @POST
    @Path("setImproper")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "USER", "ADMIN", "PUBLISHER" })
    public ImproperContent setImproperPortfolio(Portfolio portfolio) {
        return portfolioService.addImproperPortfolio(portfolio, getLoggedInUser());
    }

    @GET
    @Path("getImproper")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "ADMIN" })
    public List<ImproperContent> getImproperPortfolios() {
        return portfolioService.getImproperPortfolios();
    }

    @GET
    @Path("getDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "ADMIN" })
    public List<Portfolio> getDeletedPortfolios() {
        return portfolioService.getDeletedPortfolios();
    }

    @GET
    @Path("hasSetImproper")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "USER", "ADMIN", "PUBLISHER" })
    public Boolean hasSetImproper(@QueryParam("portfolioId") long portfolioId) {
        return portfolioService.hasSetImproper(portfolioId, getLoggedInUser());
    }

    @POST
    @Path("setNotImproper/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "ADMIN" })
    public void removeImproperPortfolios(@PathParam("id") Long id) {
        portfolioService.removeImproperPortfolios(id);
    }

    @GET
    @Path("isSetImproper")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "ADMIN" })
    public Boolean isSetImproper(@QueryParam("portfolioId") long portfolioId) {
        return portfolioService.isSetImproper(portfolioId);
    }
}
