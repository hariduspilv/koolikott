package ee.hm.dop.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.ws.message.encoder.MessageEncodingException;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.service.AuthenticatedUserService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.LoginService;
import ee.hm.dop.service.TaatService;

@Path("login")
public class LogInResource extends BaseResource {

    @Inject
    private LoginService loginService;

    @Inject
    private TaatService taatService;

    @Inject
    private HTTPRedirectDeflateEncoder encoder;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @Inject
    private LanguageService languageService;

    @GET
    @Path("/idCard")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser idCardLogin() {
        AuthenticatedUser authenticatedUser = null;

        if (isAuthValid()) {
            authenticatedUser = loginService.logIn(getIdCodeFromRequest(), getNameFromRequest(),
                    getSurnameFromRequest());
        }

        return authenticatedUser;
    }

    @GET
    @Path("/taat")
    @Produces(MediaType.APPLICATION_JSON)
    public void makeTaatRequest() throws MessageEncodingException {
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = taatService
                .buildMessageContext(getResponse());
        encoder.encode(context);
    }

    @POST
    @Path("/taat")
    public Response taatAuthenticate(MultivaluedMap<String, String> formParams) throws URISyntaxException {
        AuthenticatedUser authenticatedUser = taatService.authenticate(formParams.getFirst("SAMLResponse"),
                formParams.getFirst("RelayState"));
        URI location = new URI("../#/loginRedirect?token=" + authenticatedUser.getToken());

        return Response.temporaryRedirect(location).build();
    }

    @GET
    @Path("/getAuthenticatedUser")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser getAuthenticatedUser(@QueryParam("token") String token) {
        return authenticatedUserService.getAuthenticatedUserByToken(token);
    }

    @GET
    @Path("/mobileId")
    @Produces(MediaType.APPLICATION_JSON)
    public MobileIDSecurityCodes mobileIDAuthenticate(@QueryParam("phoneNumber") String phoneNumber,
            @QueryParam("idCode") String idCode, @QueryParam("language") String languageCode) throws Exception {
        return loginService.mobileIDAuthenticate(phoneNumber, idCode, languageService.getLanguage(languageCode));
    }

    @GET
    @Path("/mobileId/isValid")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser validateMobileIDAuthentication(@QueryParam("token") String token) throws SOAPException {
        return loginService.validateMobileIDAuthentication(token);
    }

    protected String getIdCodeFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return values[0].split("=")[1];
    }

    protected String getNameFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return values[1].split("=")[1];
    }

    protected String getSurnameFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return values[2].split("=")[1];
    }

    private boolean isAuthValid() {
        return "SUCCESS".equals(getRequest().getHeader("SSL_AUTH_VERIFY"));
    }

}
