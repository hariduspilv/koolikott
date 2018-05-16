package ee.hm.dop.rest;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.service.login.*;
import ee.hm.dop.service.login.dto.UserStatus;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import ee.hm.dop.service.metadata.LanguageService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

@Path("login")
public class LoginResource extends BaseResource {

    private static final String EKOOL_CALLBACK_PATH = "/rest/login/ekool/success";
    private static final String EKOOL_AUTHENTICATION_URL = "%s?client_id=%s&redirect_uri=%s&scope=read&response_type=code";
    private static final String STUUDIUM_AUTHENTICATION_URL = "%sclient_id=%s";
    public static final String LOGIN_REDIRECT_WITH_TOKEN = "/#!/loginRedirect?token=";
    private static final String LOGIN_REDIRECT_WITHOUT_TOKEN = "/#!/loginRedirect";
    private static final String SSL_CLIENT_S_DN = "SSL_CLIENT_S_DN";

    @Inject
    private LoginService loginService;
    @Inject
    private LoginNewService loginNewService;
    @Inject
    private EkoolService ekoolService;
    @Inject
    private StuudiumService stuudiumService;
    @Inject
    private AuthenticatedUserService authenticatedUserService;
    @Inject
    private LanguageService languageService;

    @POST
    @Path("/finalizeLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser permissionConfirm(UserStatus userStatus) {
        if (userStatus != null && userStatus.isUserConfirmed()) {
            return loginNewService.finalizeLogin(userStatus);
        }
        return null;
    }

    @GET
    @Path("/idCard")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser idCardLogin() {
        if (isAuthValid()) {
            return loginService.login(getIdCodeFromRequest(), getNameFromRequest(), getSurnameFromRequest());
        }
        return null;
    }

    @GET
    @Path("ekool")
    public Response ekoolAuthenticate() throws URISyntaxException {
        return redirect(getEkoolAuthenticationURI());
    }

    @GET
    @Path("ekool/success")
    public Response ekoolAuthenticateSuccess(@QueryParam("code") String code) throws URISyntaxException {
        return redirect(getEkoolLocation(code));
    }

    @GET
    @Path("stuudium")
    public Response stuudiumAuthenticate(@QueryParam("token") String token) throws URISyntaxException {
        return token != null ? authenticateWithStuudiumToken(token) : redirectToStuudium();
    }

    @GET
    @Path("/mobileId")
    @Produces(MediaType.APPLICATION_JSON)
    public MobileIDSecurityCodes mobileIDLogin(@QueryParam("phoneNumber") String phoneNumber,
                                               @QueryParam("idCode") String idCode,
                                               @QueryParam("language") String languageCode) throws Exception {
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

    private Response redirectToStuudium() throws URISyntaxException {
        return redirect(getStuudiumAuthenticationURI());
    }

    private Response authenticateWithStuudiumToken(String token) throws URISyntaxException {
        return redirect(getStuudiumLocation(token));
    }

    private String getIdCodeFromRequest() {
        return getString(0);
    }

    private String getNameFromRequest() {
        return getString(1);
    }

    private String getSurnameFromRequest() {
        return getString(2);
    }

    private String getString(int i) {
        String[] values = getRequest().getHeader(SSL_CLIENT_S_DN).split(",");
        return getStringInUTF8(values[i].split("=")[1]);
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
        return new URI(format(STUUDIUM_AUTHENTICATION_URL, stuudiumService.getAuthorizationUrl(), stuudiumService.getClientId()));
    }

    private String getEkoolCallbackUrl() {
        return getServerAddress() + EKOOL_CALLBACK_PATH;
    }

    private URI getStuudiumLocation(String token) throws URISyntaxException {
        try {
            AuthenticatedUser authenticatedUser = stuudiumService.authenticate(token);
            return new URI(getServerAddress() + LOGIN_REDIRECT_WITH_TOKEN + authenticatedUser.getToken());
        } catch (Exception e) {
            return new URI(getServerAddress() + LOGIN_REDIRECT_WITHOUT_TOKEN);
        }
    }

    private URI getEkoolLocation(String code) throws URISyntaxException {
        try {
            AuthenticatedUser authenticatedUser = ekoolService.authenticate(code, getEkoolCallbackUrl());
            return new URI(getServerAddress() + LOGIN_REDIRECT_WITH_TOKEN + authenticatedUser.getToken());
        } catch (Exception e) {
            return new URI(getServerAddress() + LOGIN_REDIRECT_WITHOUT_TOKEN);
        }
    }
}
