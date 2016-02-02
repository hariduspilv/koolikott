package ee.hm.dop.rest;

import static java.lang.String.format;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

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
import ee.hm.dop.service.EkoolService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.LoginService;
import ee.hm.dop.service.StuudiumService;
import ee.hm.dop.service.TaatService;

@Path("login")
public class LogInResource extends BaseResource {

    private static final String EKOOL_CALLBACK_PATH = "/rest/login/ekool/success";
    private static final String EKOOL_AUTHENTICATION_URL = "%s?client_id=%s&redirect_uri=%s&scope=read&response_type=code";

    private static final String STUUDIUM_AUTHENTICATION_URL = "%sclient_id=%s";

    @Inject
    private LoginService loginService;

    @Inject
    private TaatService taatService;

    @Inject
    private EkoolService ekoolService;

    @Inject
    private StuudiumService stuudiumService;

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
    public void taatLogin() throws MessageEncodingException {
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
    @Path("ekool")
    public Response ekoolAuthenticate() throws URISyntaxException {
        URI authenticationUri = getEkoolAuthenticationURI();
        return Response.temporaryRedirect(authenticationUri).build();
    }

    @GET
    @Path("ekool/success")
    public Response ekoolAuthenticateSuccess(@QueryParam("code") String code) throws URISyntaxException {

        try {
            AuthenticatedUser authenticatedUser = ekoolService.authenticate(code, getEkoolCallbackUrl());
            URI location = new URI("../#/loginRedirect?token=" + authenticatedUser.getToken());
            return Response.temporaryRedirect(location).build();
        } catch (Exception e) {
            URI location = new URI("../#/loginRedirect");
            return Response.temporaryRedirect(location).build();
        }
    }

    @GET
    @Path("stuudium")
    public Response stuudiumAuthenticate() throws URISyntaxException {
        URI authenticationUri = getStuudiumAuthenticationURI();
        return Response.temporaryRedirect(authenticationUri).build();
    }

    @GET
    @Path("/mobileId")
    @Produces(MediaType.APPLICATION_JSON)
    public MobileIDSecurityCodes mobileIDLogin(@QueryParam("phoneNumber") String phoneNumber,
            @QueryParam("idCode") String idCode, @QueryParam("language") String languageCode) throws Exception {
        return loginService.mobileIDAuthenticate(phoneNumber, idCode, languageService.getLanguage(languageCode));
    }

    @GET
    @Path("/mobileId/isValid")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser mobileIDAuthenticate(@QueryParam("token") String token) throws SOAPException {
        return loginService.validateMobileIDAuthentication(token);
    }

    @GET
    @Path("/getAuthenticatedUser")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser getAuthenticatedUser(@QueryParam("token") String token) {
        return authenticatedUserService.getAuthenticatedUserByToken(token);
    }

    private String getIdCodeFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return getStringInUTF8(values[0].split("=")[1]);
    }

    private String getNameFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return getStringInUTF8(values[1].split("=")[1]);
    }

    private String getSurnameFromRequest() {
        String[] values = getRequest().getHeader("SSL_CLIENT_S_DN").split(",");
        return getStringInUTF8(values[2].split("=")[1]);
    }

    private boolean isAuthValid() {
        return "SUCCESS".equals(getRequest().getHeader("SSL_AUTH_VERIFY"));
    }

    private String getStringInUTF8(String item) {
        byte[] bytes = item.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private URI getEkoolAuthenticationURI() throws URISyntaxException {
        return new URI(format(EKOOL_AUTHENTICATION_URL, ekoolService.getAuthorizationUrl(), ekoolService.getClientId(),
                getEkoolCallbackUrl()));
    }

    private URI getStuudiumAuthenticationURI() throws URISyntaxException {
        return new URI(format(STUUDIUM_AUTHENTICATION_URL, stuudiumService.getAuthorizationUrl(),
                stuudiumService.getClientId()));
    }

    private String getEkoolCallbackUrl() {
        return getServerAddress() + EKOOL_CALLBACK_PATH;
    }
}
