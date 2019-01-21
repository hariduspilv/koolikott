package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.Like;
import ee.hm.dop.service.content.*;
import ee.hm.dop.service.useractions.UserLikeService;
import ee.hm.dop.service.useractions.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("portfolio")
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

    @GetMapping
    public Portfolio get(@RequestParam("id") long portfolioId) {
        return portfolioGetter.get(portfolioId, getLoggedInUser());
    }

    @GetMapping("getByCreator")
    public SearchResult getByCreator(@RequestParam("username") String username,
                                     @RequestParam(value = "start", defaultValue = "0") int start,
                                     @RequestParam(value = "maxResults", defaultValue = "0") int maxResults) {
        User creator = getValidCreator(username);
        if (creator == null) throw badRequest("User does not exist with this username parameter");

        return portfolioGetter.getByCreatorResult(creator, getLoggedInUser(), start, maxResults);
    }

    @GetMapping("getByCreator/count")
    public Long getByCreatorCount(@RequestParam("username") String username) {
        User creator = getValidCreator(username);
        if (creator == null) throw badRequest("User does not exist with this username parameter");

        return portfolioGetter.getCountByCreator(creator);
    }

    @PostMapping("create")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Portfolio create(@RequestBody Portfolio portfolio) {
        return portfolioService.create(portfolio, getLoggedInUser());
    }

    @PostMapping("update")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Portfolio update(@RequestBody Portfolio portfolio) {
        return portfolioService.update(portfolio, getLoggedInUser());
    }

/*    @PostMapping
    @RequestMapping("copy")
    @Consumes(MediaType.APPLICATION_JSON)

    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Portfolio copy(@RequestBody Portfolio portfolio) {
        return portfolioService.copy(portfolio, getLoggedInUser());
    }*/

    @PostMapping("delete")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject delete(@RequestBody Portfolio portfolio) {
        return learningObjectAdministrationService.delete(portfolio, getLoggedInUser());
    }

    private User getValidCreator(@RequestParam("username") String username) {
        if (isBlank(username)) throw badRequest("Username parameter is mandatory");
        return userService.getUserByUsername(username);
    }
}
