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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;
import java.net.URI;
import java.net.URISyntaxException;

import static ee.hm.dop.rest.login.IdCardUtil.*;
import static java.lang.String.format;

@RestController
@RequestMapping("login")
public class LoginResource extends BaseResource {
    private final Logger logger = LoggerFactory.getLogger(LoginResource.class);

    private static final String EKOOL_CALLBACK_PATH = "/rest/login/ekool/success";
    private static final String EKOOL_AUTHENTICATION_URL = "%s?client_id=%s&redirect_uri=%s&scope=read&response_type=code";
    private static final String STUUDIUM_AUTHENTICATION_URL = "%sclient_id=%s";
    private static final String HARID_AUTHENTICATION_URL = "%s?client_id=%s&redirect_uri=%s&scope=openid+profile+personal_code&response_type=code";
    private static final String HARID_AUTHENTICATION_SUCCESS_URL = "/rest/login/harid/success";
    public static final String LOGIN_REDIRECT_WITH_TOKEN_AGREEMENT = "%s/#!/loginRedirect?token=%s&userConfirmed=%s&statusOk=%s&agreement=%s&gdprAgreement=%s&existingUser=%s&loginFrom=%s";
    public static final String LOGIN_REDIRECT_WITH_TOKEN = "%s/#!/loginRedirect?token=%s";
    public static final String LOGIN_REDIRECT_WITHOUT_TOKEN = "%s/#!/loginRedirect";
    public static final String LOGIN_REDIRECT_WITHOUT_IDCODE_EKOOL = "%s/#!/loginRedirect?eKoolUserMissingIdCode=%s";
    public static final String LOGIN_REDIRECT_WITHOUT_IDCODE_STUUDIUM = "%s/#!/loginRedirect?stuudiumUserMissingIdCode=%s";
    public static final String LOGIN_REDIRECT_WITHOUT_IDCODE_HARID= "%s/#!/loginRedirect?harIdUserMissingIdCode=%s";

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

    @PostMapping("/finalizeLogin")
    public AuthenticatedUser permissionConfirm(@RequestBody UserStatus userStatus) {
        return confirmed(userStatus) ? loginService.finalizeLogin(userStatus) : null;
    }

    @PostMapping("/rejectAgreement")
    public void permissionReject(@RequestBody UserStatus userStatus) {
        if (userStatus.isExistingUser()) {
            loginService.rejectAgreement(userStatus);
        }
    }

    @GetMapping("/idCard")
    public UserStatus idCardLogin() {
        HttpServletRequest req = getRequest();
        logger.info(req.getHeader(SSL_CLIENT_S_DN));
        IdCardInfo info = getInfo(req);
        logger.info(info.toString());
        return isAuthValid(req) ? loginService.login(info.getIdCode(), info.getFirstName(), info.getSurName(), LoginFrom.ID_CARD) : null;
    }

    @GetMapping("ekool")
    public RedirectView ekoolAuthenticate() throws URISyntaxException {
        return new RedirectView(getEkoolAuthenticationURI().toString());
    }

    @GetMapping
    @RequestMapping("ekool/success")
    public RedirectView ekoolAuthenticateSuccess(@RequestParam("code") String code) throws URISyntaxException {
        return new RedirectView(getEkoolLocation(code).toString());
    }

    @GetMapping
    @RequestMapping("stuudium")
    public RedirectView stuudiumAuthenticate(@RequestParam(value = "token", required = false) String token) throws URISyntaxException {
        return token != null ? authenticateWithStuudiumToken(token) : redirectToStuudium();
    }

    @GetMapping
    @RequestMapping("harid")
    public RedirectView haridAuthenticate() throws URISyntaxException {
        return new RedirectView(getHaridAuthenticationURI().toString());
    }

    @GetMapping
    @RequestMapping("harid/success")
    public RedirectView haridAuthenticateSuccess(@RequestParam(value = "code", required = false) String code) throws URISyntaxException {
        return code != null ? authenticateWithHaridToken(code) : redirectToHarid();
    }

    @GetMapping
    @RequestMapping("/mobileId")
    public MobileIDSecurityCodes mobileIDLogin(@RequestParam("phoneNumber") String phoneNumber,
                                               @RequestParam("idCode") String idCode,
                                               @RequestParam("language") String languageCode) throws Exception {
        return mobileIDLoginService.authenticate(phoneNumber, idCode, languageService.getLanguage(languageCode));
    }

    @GetMapping("/mobileId/isValid")
    public UserStatus mobileIDAuthenticate(@RequestParam("token") String token) throws SOAPException {
        UserStatus userStatus = mobileIDLoginService.validateMobileIDAuthentication(token);
        logger.info("userstatus is {}", userStatus);
        return userStatus;
    }

    @GetMapping
    @RequestMapping("/getAuthenticatedUser")
    public AuthenticatedUser getAuthenticatedUser(@RequestParam("token") String token) {
        return authenticatedUserService.getAuthenticatedUserByToken(token);
    }

    private RedirectView redirectToStuudium() throws URISyntaxException {
        return new RedirectView(getStuudiumAuthenticationURI().toString());
    }

    private RedirectView authenticateWithStuudiumToken(String token) throws URISyntaxException {
        return new RedirectView(getStuudiumLocation(token).toString());
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

    private RedirectView redirectToHarid() throws URISyntaxException {
        return new RedirectView(getHaridAuthenticationURI().toString());
    }

    private URI getHaridAuthenticationURI() throws URISyntaxException {
        return new URI(format(HARID_AUTHENTICATION_URL, haridService.getAuthorizationUrl(), haridService.getClientId(), getHaridCallbackUrl()));
    }

    private RedirectView authenticateWithHaridToken(String token) throws URISyntaxException {
        return new RedirectView(getHaridLocation(token).toString());
    }

    private String getHaridCallbackUrl() {
        return getServerAddress() + HARID_AUTHENTICATION_SUCCESS_URL;
    }

    private URI getHaridLocation(String token) throws URISyntaxException {
        try {
            return redirectSuccess(haridService.authenticate(token, getHaridCallbackUrl()));
        } catch (Exception e) {
            logger.error("harId login failed", e);
            return redirectFailure();
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
            return new URI(format(LOGIN_REDIRECT_WITH_TOKEN, getServerAddress(), status.getAuthenticatedUser().getToken()));
        }
        if (status.isEKoolUserMissingIdCode()) {
            return new URI(format(LOGIN_REDIRECT_WITHOUT_IDCODE_EKOOL, getServerAddress(), true));
        }
        if (status.isStuudiumUserMissingIdCode()) {
            return new URI(format(LOGIN_REDIRECT_WITHOUT_IDCODE_STUUDIUM, getServerAddress(), true));
        }
        if (status.isHarIdUserMissingIdCode()) {
            return new URI(format(LOGIN_REDIRECT_WITHOUT_IDCODE_HARID, getServerAddress(), true));
        }
        return new URI(format(LOGIN_REDIRECT_WITH_TOKEN_AGREEMENT, getServerAddress(), status.getToken(), status.isUserConfirmed(), status.isStatusOk(), status.getUserTermsAgreement().getId().toString(), status.getGdprTermsAgreement().getId().toString(), status.isExistingUser(), status.getLoginFrom().name()));
    }

    private URI redirectFailure() throws URISyntaxException {
        return new URI(format(LOGIN_REDIRECT_WITHOUT_TOKEN, getServerAddress()));
    }

    private boolean confirmed(UserStatus userStatus) {
        return userStatus != null && userStatus.isUserConfirmed();
    }
}
