package ee.hm.dop.rest;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioLog;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.content.PortfolioCopier;
import ee.hm.dop.service.content.PortfolioGetter;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.service.useractions.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

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

    @GetMapping("getPortfolioHistoryAll")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public List<PortfolioLog> getPortfolioHistoryAll(@RequestParam("portfolioId") Long portfolioId) {
        List<PortfolioLog> portfolioLog;
        if (getLoggedInUser().getRole().equals(Role.ADMIN)) {
            portfolioLog = portfolioGetter.getPortfolioHistoryAll(portfolioId);
        } else if (getLoggedInUser().getRole().equals(Role.MODERATOR)) {
            portfolioLog = portfolioGetter.findByIdAndCreatorAllPortfolioLogs(portfolioId, getLoggedInUser());
        } else {
            throw new WebApplicationException("User has no access", Response.Status.FORBIDDEN);
        }

        if (portfolioLog == null || isEmpty(portfolioLog))
            throw badRequest("No portfoliologs for portfolio with id: " + portfolioId);
        return portfolioLog;
    }

    @GetMapping("getPortfolioHistory")
    public PortfolioLog getPortfolioHistory(@RequestParam("portfolioHistoryId") Long portfolioHistoryId) {
        PortfolioLog portfolioLog = portfolioGetter.getPortfolioHistory(portfolioHistoryId);
        if (portfolioLog == null) throw badRequest("No such portfoliolog with id: " + portfolioHistoryId);
        return portfolioLog;
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
