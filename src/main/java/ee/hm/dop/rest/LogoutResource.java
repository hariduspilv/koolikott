package ee.hm.dop.rest;

import static java.lang.String.format;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.rest.filter.DopPrincipal;
import ee.hm.dop.service.LogoutService;

@Path("logout")
@PermitAll
public class LogoutResource {

    private static Logger logger = LoggerFactory.getLogger(LogoutResource.class);

    @Inject
    private LogoutService logoutService;

    @Context
    private HttpServletRequest request;

    private SecurityContext securityContext;

    @Context
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @POST
    public void logout() {
        DopPrincipal dopPrincipal = (DopPrincipal) securityContext.getUserPrincipal();
        logoutService.logout(dopPrincipal.getAuthenticatedUser());
        logger.info(format("User %s is logged out using id card login with id %s.",
                dopPrincipal.getUser().getUsername(),
                dopPrincipal.getUser().getIdCode()));
    }
}
