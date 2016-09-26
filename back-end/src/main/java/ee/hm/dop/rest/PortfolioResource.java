package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sound.sampled.Port;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import ee.hm.dop.model.UserLike;
import ee.hm.dop.service.LearningObjectService;
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

        return portfolioService.get(portfolioId, getLoggedInUser());
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

    @POST
    @Path("increaseViewCount")
    public void increaseViewCount(Portfolio portfolio) {
        portfolioService.incrementViewCount(portfolio);
    }

    @POST
    @Path("like")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public void likePortfolio(Portfolio portfolio) {
        portfolioService.addUserLike(portfolio, getLoggedInUser(), true);
    }

    @POST
    @Path("dislike")
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public void dislikePortfolio(Portfolio portfolio) {
        portfolioService.addUserLike(portfolio, getLoggedInUser(), false);
    }

    @POST
    @Path("recommend")
    @RolesAllowed({"ADMIN"})
    public Recommendation recommendPortfolio(Portfolio portfolio) {
        return portfolioService.addRecommendation(portfolio, getLoggedInUser());
    }

    @POST
    @Path("removeRecommendation")
    @RolesAllowed({"ADMIN"})
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
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public Portfolio create(Portfolio portfolio) {
        return portfolioService.create(portfolio, getLoggedInUser());
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public Portfolio update(Portfolio portfolio) {
        return portfolioService.update(portfolio, getLoggedInUser());
    }

    @POST
    @Path("copy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public Portfolio copy(Portfolio portfolio) {
        return portfolioService.copy(portfolio, getLoggedInUser());
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    public void delete(Portfolio portfolio) {
        portfolioService.delete(portfolio, getLoggedInUser());
    }

    @POST
    @Path("restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public void restore(Portfolio portfolio) {
        portfolioService.restore(portfolio, getLoggedInUser());
    }

    @GET
    @Path("getDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public List<Portfolio> getDeletedPortfolios() {
        return portfolioService.getDeletedPortfolios();
    }
}
