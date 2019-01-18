package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.service.login.LoginService;
import ee.hm.dop.service.login.dto.UserStatus;
import org.apache.commons.configuration2.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static java.lang.String.format;

@Path("dev/")
@Profile("test")
public class DevelopmentLoginResource {

    @Inject
    private LoginService loginService;
    @Inject
    private Configuration configuration;

    @GET
    @Path("/login/{idCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserStatus logIn(@PathParam("idCode") String idCode) {
        if (configuration.getBoolean("app.devLogin")) {
            return loginService.login(idCode, null, null, LoginFrom.DEV);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
