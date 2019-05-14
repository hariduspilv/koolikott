package ee.hm.dop.rest;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.content.PortfolioCopier;
import ee.hm.dop.service.content.PortfolioGetter;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.service.useractions.UserService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("portfolio")
public class PortfolioResource extends BaseResource {

    @Inject
    private PortfolioService portfolioService;
    @Inject
    private UserService userService;
    @Inject
    private PortfolioCopier portfolioCopier;
    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;
    @Inject
    private PortfolioGetter portfolioGetter;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Portfolio get(@QueryParam("id") long portfolioId) {
        return portfolioGetter.get(portfolioId, getLoggedInUser());
    }

    @GET
    @Path("getByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult getByCreator(@QueryParam("username") String username, @QueryParam("start") int start, @QueryParam("maxResults") int maxResults) {
        User creator = getValidCreator(username);
        if (creator == null) throw badRequest("User does not exist with this username parameter");

        return portfolioGetter.getByCreatorResult(creator, getLoggedInUser(), start, maxResults);
    }

    @GET
    @Path("getPortfolioHistoryAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PortfolioLog> getPortfolioHistoryAll(@QueryParam("portfolioId") long portfolioId) {
        return portfolioGetter.getPortfolioHistoryAll(portfolioId);//TODO check such id
    }

    @GET
    @Path("getPortfolioHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public PortfolioLog getPortfolioHistory(@QueryParam("portfolioHistoryId") long portfolioHistoryId) {
        return portfolioGetter.getPortfolioHistory(portfolioHistoryId);//TODO check such id
    }

//    @POST
//    @Path("history/restore")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Portfolio setPortfolioLogToPortfolio(@QueryParam("id") long id) {
//        return portfolioGetter.setPortfolioHistory(id);//TODO check such id
//    }

    @GET
    @Path("getByCreator/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Long getByCreatorCount(@QueryParam("username") String username) {
        User creator = getValidCreator(username);
        if (creator == null) throw badRequest("User does not exist with this username parameter");
        return portfolioGetter.getCountByCreator(creator);
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

/*    @POST
    @Path("copy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Portfolio copy(Portfolio portfolio) {
        return portfolioService.copy(portfolio, getLoggedInUser());
    }*/

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject delete(Portfolio portfolio) {
        return learningObjectAdministrationService.delete(portfolio, getLoggedInUser());
    }

    private User getValidCreator(@QueryParam("username") String username) {
        if (isBlank(username)) throw badRequest("Username parameter is mandatory");
        return userService.getUserByUsername(username);
    }
}
