package ee.hm.dop.rest;

import static java.lang.String.format;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.LoginService;
import ee.hm.dop.service.TaatService;
import ee.hm.dop.service.UserService;

@Path("login")
public class LogInResource {

    private static Logger logger = LoggerFactory.getLogger(LogInResource.class);

    @Inject
    private LoginService loginService;

    @Inject
    private TaatService taatService;

    @Inject
    private HTTPRedirectDeflateEncoder encoder;

    @Inject
    private UserService userService;

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

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
                logger.info(format("User with id %s could not log in, trying to create account. ", idCode));

                // Create new user account
                userService.create(idCode, getNameFromRequest(), getSurnameFromRequest());
                authenticatedUser = loginService.logIn(idCode);

                if (authenticatedUser == null) {
                    throw new RuntimeException(
                            format("User with id %s tried to log in after creating account, but failed.", idCode));
                }

                authenticatedUser.setFirstLogin(true);
                logger.info(format("User %s logged in for the first time using id card login with id %s.",
                        authenticatedUser.getUser().getUsername(), idCode));
            }
        }

        return authenticatedUser;
    }

    @GET
    @Path("/taat")
    @Produces(MediaType.APPLICATION_JSON)
    public void makeTaatRequest() {
        AuthnRequest authnRequest = taatService.buildAuthnRequest();
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = taatService
                .buildMessageContext(authnRequest, response);

        try {
            encoder.encode(context);
        } catch (MessageEncodingException e) {
            logger.error("Error while encoding SAML message context.");
        }
    }

    @POST
    @Path("/taat")
    public Response taatAuthenticate(MultivaluedMap<String, String> formParams) throws URISyntaxException {
        AuthenticatedUser authenticatedUser = taatService.authenticate(formParams.getFirst("SAMLResponse"));
        URI location = new URI("../#/loginRedirect?token=" + authenticatedUser.getToken());

        return Response.temporaryRedirect(location).build();
    }

    protected String getIdCodeFromRequest() {
        String[] values = request.getHeader("SSL_CLIENT_S_DN").split(",");
        return values[0].split("=")[1];
    }

    protected String getNameFromRequest() {
        String[] values = request.getHeader("SSL_CLIENT_S_DN").split(",");
        return values[1].split("=")[1];
    }

    protected String getSurnameFromRequest() {
        String[] values = request.getHeader("SSL_CLIENT_S_DN").split(",");
        return values[2].split("=")[1];
    }

    private boolean isAuthValid() {
        return "SUCCESS".equals(request.getHeader("SSL_AUTH_VERIFY"));
    }

}
