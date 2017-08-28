package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.service.useractions.UserService;

@Path("portfolio")
public class PortfolioResource extends BaseResource {

    @Inject
    private PortfolioService portfolioService;

    @Inject
    private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Portfolio get(@QueryParam("id") long portfolioId) {

        return portfolioService.get(portfolioId, getLoggedInUser());
    }

    @GET
    @Path("getByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult getByCreator(@QueryParam("username") String username, @QueryParam("start") int start, @QueryParam("maxResults") int maxResults) {
        if (isBlank(username)) throwBadRequestException("Username parameter is mandatory");

        User creator = userService.getUserByUsername(username);
        if (creator == null) throwBadRequestException("User does not exist with this username parameter");

        User loggedInUser = getLoggedInUser();

        List<Searchable> searchables = new ArrayList<>(portfolioService.getByCreator(creator, loggedInUser, start, maxResults));
        Long size = portfolioService.getCountByCreator(creator);
        return new SearchResult(searchables, size, start);

    }

    @GET
    @Path("getByCreator/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByCreatorCount(@QueryParam("username") String username) {
        if (isBlank(username)) throwBadRequestException("Username parameter is mandatory");

        User creator = userService.getUserByUsername(username);
        if (creator == null) throwBadRequestException("User does not exist with this username parameter");

        return Response.ok(portfolioService.getCountByCreator(creator)).build();
    }

    @POST
    @Path("increaseViewCount")
    public void increaseViewCount(Portfolio portfolio) {
        portfolioService.incrementViewCount(portfolio);
    }

    @POST
    @Path("like")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void likePortfolio(Portfolio portfolio) {
        portfolioService.addUserLike(portfolio, getLoggedInUser(), true);
    }

    @POST
    @Path("dislike")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void dislikePortfolio(Portfolio portfolio) {
        portfolioService.addUserLike(portfolio, getLoggedInUser(), false);
    }

    @POST
    @Path("recommend")
    @RolesAllowed({RoleString.ADMIN})
    public Recommendation recommendPortfolio(Portfolio portfolio) {
        return portfolioService.addRecommendation(portfolio, getLoggedInUser());
    }

    @POST
    @Path("removeRecommendation")
    @RolesAllowed({RoleString.ADMIN})
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
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Portfolio create(Portfolio portfolio) {
        return portfolioService.create(portfolio, getLoggedInUser());
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Portfolio update(Portfolio portfolio) {
        return portfolioService.update(portfolio, getLoggedInUser());
    }

    @POST
    @Path("copy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Portfolio copy(Portfolio portfolio) {
        return portfolioService.copy(portfolio, getLoggedInUser());
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void delete(Portfolio portfolio) {
        portfolioService.delete(portfolio, getLoggedInUser());
    }

    @POST
    @Path("restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void restore(Portfolio portfolio) {
        portfolioService.restore(portfolio, getLoggedInUser());
    }

    @GET
    @Path("getDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<Portfolio> getDeletedPortfolios() {
        return portfolioService.getDeletedPortfolios();
    }

    @GET
    @Path("getDeleted/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Response getDeletedPortfoliosCount() {
        return Response.ok(portfolioService.getDeletedPortfoliosCount()).build();
    }
}
