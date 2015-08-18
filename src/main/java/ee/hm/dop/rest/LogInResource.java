package ee.hm.dop.rest;

import java.net.HttpURLConnection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.LoginService;

/**
 * Created by mart.laus on 13.08.2015.
 */
@Path("{idCode}")
public class LogInResource {

    @Inject
    private LoginService loginService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser logIn(@PathParam("idCode") String idCode) {
        return loginService.logIn(idCode);
    }
}
