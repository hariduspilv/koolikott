package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.LoginService;
import ee.hm.dop.service.LoginService.LoginForm;

/**
 * Created by mart on 17.08.15.
 */

@Path("dev/")
public class DevelopmentLoginResource {

    @Inject
    private LoginService loginService;

    @GET
    @Path("/login/{idCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser logIn(@PathParam("idCode") String idCode) {
        LoginForm loginForm = new LoginForm(idCode, null, null);
        return loginService.logIn(loginForm);
    }
}
