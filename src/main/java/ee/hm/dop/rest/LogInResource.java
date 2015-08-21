package ee.hm.dop.rest;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.*;

/**
 * Created by mart.laus on 13.08.2015.
 */
@Path("/login")
public class LogInResource {
    @Inject
    private LoginService loginService;

    @Context
    private HttpServletRequest request;

    private static Logger logger = LoggerFactory.getLogger(LogInResource.class);

    @GET
    @Path("/idCard")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser idCardLogin() {
        AuthenticatedUser authenticatedUser = null;

        if (isAuthValid()) {
            String idCode = getIdCodeFromRequest();
            authenticatedUser = loginService.logIn(idCode);

            if (authenticatedUser != null) {
                logger.info(format("User %s is logged in using id card login with id %s.",
                        authenticatedUser.getUser().getUsername(), idCode));
            } else {
                logger.info(format("User with id %s tried to log in, but failed.", idCode));
            }
        }

        return authenticatedUser;
    }

    protected String getIdCodeFromRequest() {
        String[] values = request.getHeader("SSL_CLIENT_S_DN").split(",");
        return values[0].split("=")[1];
    }

    private boolean isAuthValid() {
            return "SUCCESS".equals(request.getHeader("SSL_AUTH_VERIFY"));
    }


}
