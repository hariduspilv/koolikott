package ee.hm.dop.rest.login;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.user.UserSession;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.SessionService;
import ee.hm.dop.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static ee.hm.dop.service.login.SessionUtil.minRemaining;
import static ee.hm.dop.utils.ConfigurationProperties.SESSION_ALERT_MINS;
import static java.lang.String.format;

@RestController
@RequestMapping("user")
@Secured({ RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR })
public class SessionResource extends BaseResource {

    private static Logger logger = LoggerFactory.getLogger(SessionResource.class);

    @Inject
    private SessionService sessionService;
    @Inject
    private Configuration configuration;

    @GetMapping
    @RequestMapping("sessionTime")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public UserSession getSessionTime() {
        AuthenticatedUser user = getAuthenticatedUser();
        return new UserSession(minRemaining(user), !user.isDeclined());
    }

    @GetMapping
    @RequestMapping(value = "sessionAlertTime", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getSessionAlertTime() {
        return configuration.getString(SESSION_ALERT_MINS);
    }

    @PostMapping
    @RequestMapping("updateSession")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public UserSession updateSessionTime(@RequestBody UserSession userSession) {
        AuthenticatedUser user = sessionService.updateSession(userSession, getAuthenticatedUser());
        return new UserSession(minRemaining(user), !user.isDeclined());
    }

    @PostMapping
    @RequestMapping("terminateSession")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public void terminateSession() {
        sessionService.terminateSession(getAuthenticatedUser());
    }

    @PostMapping
    @RequestMapping("logout")
    public void logout() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        sessionService.logout(authenticatedUser);
        logger.info(format("User %s is logged out", authenticatedUser.getUser().getUsername()));
    }
}
