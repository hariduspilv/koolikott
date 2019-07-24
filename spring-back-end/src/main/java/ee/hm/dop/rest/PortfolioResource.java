package ee.hm.dop.rest;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.content.PortfolioCopier;
import ee.hm.dop.service.content.PortfolioGetter;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.service.useractions.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
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
        if (creator == null) throw notFound();

        return portfolioGetter.getByCreatorResult(creator, getLoggedInUser(), start, maxResults);
    }

    @GetMapping("getByCreator/count")
    public Long getByCreatorCount(@RequestParam("username") String username) {
        User creator = getValidCreator(username);
        if (creator == null) throw notFound();

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
    @Secured({RoleString.ADMIN, RoleString.MODERATOR,RoleString.USER})
    public List<PortfolioLog> getPortfolioHistoryAll(@RequestParam("portfolioId") Long portfolioId) {
        List<PortfolioLog> portfolioLog;
        if (getLoggedInUser().getRole().equals(Role.ADMIN)) {
            portfolioLog = portfolioGetter.getPortfolioHistoryAll(portfolioId);
        } else if (getLoggedInUser().getRole().equals(Role.MODERATOR) || getLoggedInUser().getRole().equals(Role.USER)) {
            portfolioLog = portfolioGetter.findByIdAndCreatorAllPortfolioLogs(portfolioId, getLoggedInUser());
        } else {
            throw forbidden("User has no access");
        }

        if (portfolioLog == null || isEmpty(portfolioLog))
            throw badRequest("No portfoliologs for portfolio with id: " + portfolioId);
        return portfolioLog;
    }

    @GetMapping("getPortfolioHistory")
    public PortfolioLog getPortfolioHistory(@RequestParam("portfolioHistoryId") Long portfolioHistoryId) {
        PortfolioLog portfolioLog = portfolioGetter.getPortfolioHistory(portfolioHistoryId);
        if (portfolioLog == null) throw notFound();
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
        if (isBlank(username)) throw notFound();
        return userService.getUserByUsername(username);
    }
}
