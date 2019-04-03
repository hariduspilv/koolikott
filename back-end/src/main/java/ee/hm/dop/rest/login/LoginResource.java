package ee.hm.dop.rest.login;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.login.*;
import ee.hm.dop.service.login.dto.IdCardInfo;
import ee.hm.dop.service.login.dto.UserStatus;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.soap.SOAPException;
import java.net.URI;
import java.net.URISyntaxException;

import static ee.hm.dop.rest.login.IdCardUtil.SSL_CLIENT_S_DN;
import static ee.hm.dop.rest.login.IdCardUtil.getInfo;
import static ee.hm.dop.rest.login.IdCardUtil.isAuthValid;
import static java.lang.String.format;

@Path("login")
public class LoginResource extends BaseResource {
    private final Logger logger = LoggerFactory.getLogger(LoginResource.class);

    private static final String EKOOL_CALLBACK_PATH = "/rest/login/ekool/success";
    private static final String EKOOL_AUTHENTICATION_URL = "%s?client_id=%s&redirect_uri=%s&scope=read&response_type=code";
    private static final String STUUDIUM_AUTHENTICATION_URL = "%sclient_id=%s";
    private static final String HARID_AUTHENTICATION_URL = "%s?client_id=%s&redirect_uri=%s&scope=personal_code&response_type=code";
//    private static final String HARID_AUTHENTICATION_URL = "https://test.harid.ee/et/authorizations/new";
    private static final String HARID_AUTHENTICATION_SUCCESS_URL = "/rest/login/harid/success";
//    private static final String HARID_AUTHENTICATION_SUCCESS_URL = "https://oxygen.netgroupdigital.com/rest/login/harid/success";
    private static final String HARID_AUTHENTICATION_FAILURE_URL = "/rest/login/harid/fail";
//    private static final String HARID_AUTHENTICATION_FAILURE_URL = "https://oxygen.netgroupdigital.com/rest/login/harid/failure";
    public static final String LOGIN_REDIRECT_WITH_TOKEN_AGREEMENT = "%s/#!/loginRedirect?token=%s&agreement=%s&existingUser=%s&loginFrom=%s";
    public static final String LOGIN_REDIRECT_WITH_TOKEN = "%s/#!/loginRedirect?token=%s";
    public static final String LOGIN_REDIRECT_WITHOUT_TOKEN = "%s/#!/loginRedirect";
    public static final String LOGIN_REDIRECT_WITHOUT_IDCODE_EKOOL = "%s/#!/loginRedirect?eKoolUserMissingIdCode=%s";
    public static final String LOGIN_REDIRECT_WITHOUT_IDCODE_STUUDIUM = "%s/#!/loginRedirect?stuudiumUserMissingIdCode=%s";

    @Inject
    private LoginService loginService;
    @Inject
    private EkoolService ekoolService;
    @Inject
    private StuudiumService stuudiumService;
    @Inject
    private AuthenticatedUserService authenticatedUserService;
    @Inject
    private LanguageService languageService;
    @Inject
    private MobileIDLoginService mobileIDLoginService;
    @Inject
    private HaridService haridService;

    @POST
    @Path("/finalizeLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser permissionConfirm(UserStatus userStatus) {
        return confirmed(userStatus) ? loginService.finalizeLogin(userStatus) : null;
    }

    @POST
    @Path("/rejectAgreement")
    @Produces(MediaType.APPLICATION_JSON)
    public void permissionReject(UserStatus userStatus) {
        if (userStatus.isExistingUser()) {
            loginService.rejectAgreement(userStatus);
        }
    }

    @GET
    @Path("/idCard")
    @Produces(MediaType.APPLICATION_JSON)
    public UserStatus idCardLogin() {
        HttpServletRequest req = getRequest();
        logger.info(req.getHeader(SSL_CLIENT_S_DN));
        IdCardInfo info = getInfo(req);
        logger.info(info.toString());
        return isAuthValid(req) ? loginService.login(info.getIdCode(), info.getFirstName(), info.getSurName(), LoginFrom.ID_CARD) : null;
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
    @Path("harid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response haridAuthenticate() throws URISyntaxException {
        return redirect(getHaridAuthenticationURI());
    }

    @GET
    @Path("harid/success")
    @Produces(MediaType.APPLICATION_JSON)
    public Response haridAuthenticateSuccess(@QueryParam("code") String code) throws URISyntaxException {
        return code != null ? authenticateWithHaridToken(code) : redirectToHarid();
    }

    @GET
    @Path("harid/failure")
    public URI haridAuthenticateFailure() throws URISyntaxException {
        return redirectFailure();
    }

    @GET
    @Path("/mobileId")
    @Produces(MediaType.APPLICATION_JSON)
    public MobileIDSecurityCodes mobileIDLogin(@QueryParam("phoneNumber") String phoneNumber,
                                               @QueryParam("idCode") String idCode,
                                               @QueryParam("language") String languageCode) throws Exception {
        return mobileIDLoginService.authenticate(phoneNumber, idCode, languageService.getLanguage(languageCode));
    }

    @GET
    @Path("/mobileId/isValid")
    @Produces(MediaType.APPLICATION_JSON)
    public UserStatus mobileIDAuthenticate(@QueryParam("token") String token) throws SOAPException {
        return mobileIDLoginService.validateMobileIDAuthentication(token);
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

    private URI getEkoolAuthenticationURI() throws URISyntaxException {
        return new URI(format(EKOOL_AUTHENTICATION_URL, ekoolService.getAuthorizationUrl(), ekoolService.getClientId(), getEkoolCallbackUrl()));
    }

    private URI getStuudiumAuthenticationURI() throws URISyntaxException {
        return new URI(format(STUUDIUM_AUTHENTICATION_URL, stuudiumService.getAuthorizationUrl(), stuudiumService.getClientId()));
    }

    private String getEkoolCallbackUrl() {
        return getServerAddress() + EKOOL_CALLBACK_PATH;
    }

    private Response redirectToHarid() throws URISyntaxException {
        return redirect(getHaridAuthenticationURI());
    }

    private URI getHaridAuthenticationURI() throws URISyntaxException {
        return new URI(format(HARID_AUTHENTICATION_URL, haridService.getAuthorizationUrl(), haridService.getClientId(), getHaridCallbackUrl()));
    }

    private Response authenticateWithHaridToken(String token) throws URISyntaxException {
        return redirect(getHaridLocation(token));
    }

    private String getHaridCallbackUrl() {
        logger.info(getServerAddress() + HARID_AUTHENTICATION_SUCCESS_URL);
        return getServerAddress() + HARID_AUTHENTICATION_SUCCESS_URL;
    }

    private URI getHaridLocation(String token) throws URISyntaxException {
        try {
            logger.info("token: " + token + "URL= " + getHaridCallbackUrl());
            return redirectSuccess(haridService.authenticate(token,getHaridCallbackUrl()));
        } catch (Exception e) {
            logger.error("harId login failed", e);
//            return new URI(HARID_AUTHENTICATION_FAILURE_URL);
            return new URI(getServerAddress() + HARID_AUTHENTICATION_FAILURE_URL);
        }
    }

    private URI getStuudiumLocation(String token) throws URISyntaxException {
        try {
            return redirectSuccess(stuudiumService.authenticate(token));
        } catch (Exception e) {
            logger.error("stuudium login failed", e);
            return redirectFailure();
        }
    }

    private URI getEkoolLocation(String code) throws URISyntaxException {
        try {
            return redirectSuccess(ekoolService.authenticate(code, getEkoolCallbackUrl()));
        } catch (Exception e) {
            logger.error("ekool login failed", e);
            return redirectFailure();
        }
    }

    private URI redirectSuccess(UserStatus status) throws URISyntaxException {
        if (status.isStatusOk()) {
            logger.info("Status: "+ status.toString());
            return new URI(format(LOGIN_REDIRECT_WITH_TOKEN, getServerAddress(), status.getAuthenticatedUser().getToken()));
        }
        if (status.iseKoolUserMissingIdCode()) {
            return new URI(format(LOGIN_REDIRECT_WITHOUT_IDCODE_EKOOL, getServerAddress(), true));
        }
        if (status.isStuudiumUserMissingIdCode()) {
            return new URI(format(LOGIN_REDIRECT_WITHOUT_IDCODE_STUUDIUM, getServerAddress(), true));
        }

        return new URI(format(LOGIN_REDIRECT_WITH_TOKEN_AGREEMENT, getServerAddress(), status.getToken(), status.getAgreementId().toString(), status.isExistingUser(), status.getLoginFrom().name()));
    }

    private URI redirectFailure() throws URISyntaxException {
        return new URI(format(LOGIN_REDIRECT_WITHOUT_TOKEN, getServerAddress()));
    }

    private boolean confirmed(UserStatus userStatus) {
        return userStatus != null && userStatus.isUserConfirmed();
    }
}
