package ee.hm.dop.service.login;

import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.model.mobileid.soap.MobileAuthenticateResponse;
import ee.hm.dop.service.login.dto.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.xml.soap.SOAPException;

public class MobileIDLoginService {

    private static Logger logger = LoggerFactory.getLogger(MobileIDLoginService.class);
    protected static final String ESTONIAN_CALLING_CODE = "+372";

    @Inject
    private MobileIDSOAPService mobileIDSOAPService;
    @Inject
    private AuthenticationStateDao authenticationStateDao;
    @Inject
    private AuthenticationStateService authenticationStateService;
    @Inject
    private LoginService loginService;

    public UserStatus validateMobileIDAuthentication(String token) throws SOAPException {
        if (!isAuthenticated(token)) {
            logger.info("Authentication not valid.");
            return null;
        }
        AuthenticationState authenticationState = authenticationStateDao.findAuthenticationStateByToken(token);
        return login(authenticationState, LoginFrom.MOB_ID);
    }

    public UserStatus login(AuthenticationState authenticationState, LoginFrom loginFrom) {
        if (authenticationState == null) {
            return null;
        }

        if (loginService.hasExpired(authenticationState)) {
            authenticationStateDao.delete(authenticationState);
            return null;
        }

        UserStatus authenticatedUser = loginService.login(authenticationState.getIdCode(), authenticationState.getName(), authenticationState.getSurname(), loginFrom);
        authenticationStateDao.delete(authenticationState);
        return authenticatedUser;
    }

    public MobileIDSecurityCodes authenticate(String phoneNumber, String idCode, Language language) throws Exception {
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = ESTONIAN_CALLING_CODE + phoneNumber;
        }
        if (!phoneNumber.startsWith(ESTONIAN_CALLING_CODE)) {
            logger.info("Non-Estonian mobile numbers are not allowed.");
            return null;
        }

        MobileAuthenticateResponse mobileAuthenticateResponse = mobileIDSOAPService.authenticate(phoneNumber, idCode, language);
        if (mobileAuthenticateResponse == null) {
            return null;
        }

        AuthenticationState authenticationState = authenticationStateService.save(mobileAuthenticateResponse);

        MobileIDSecurityCodes mobileIDSecurityCodes = new MobileIDSecurityCodes();
        mobileIDSecurityCodes.setChallengeId(mobileAuthenticateResponse.getChallengeID());
        mobileIDSecurityCodes.setToken(authenticationState.getToken());
        return mobileIDSecurityCodes;
    }

    public boolean isAuthenticated(String token) throws SOAPException {
        AuthenticationState authenticationState = authenticationStateDao.findAuthenticationStateByToken(token);
        if (authenticationState == null) {
            logger.info("Invalid token.");
            return false;
        }
        return mobileIDSOAPService.isAuthenticated(authenticationState);
    }

}
