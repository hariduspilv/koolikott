package ee.hm.dop.service;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.inject.Inject;
import javax.xml.soap.SOAPException;

import org.joda.time.DateTime;

import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.mobileid.MobileAuthenticateResponse;

public class MobileIDLoginService {

    @Inject
    private MobileIDSOAPService mobileIDSOAPService;

    @Inject
    private AuthenticationStateDAO authenticationStateDAO;

    private SecureRandom random = new SecureRandom();

    public MobileAuthResponse authenticate(String phoneNumber, String idCode, Language language) throws Exception {
        MobileAuthenticateResponse mobileAuthenticateResponse = mobileIDSOAPService.authenticate(phoneNumber, idCode,
                language);

        AuthenticationState authenticationState = saveResponseToAuthenticationState(mobileAuthenticateResponse);

        MobileAuthResponse mobileAuthResponse = new MobileAuthResponse();
        mobileAuthResponse.setChallengeId(mobileAuthenticateResponse.getChallengeID());
        mobileAuthResponse.setToken(authenticationState.getToken());
        return mobileAuthResponse;
    }

    public boolean isAuthenticated(String token) throws SOAPException {
        AuthenticationState authenticationState = authenticationStateDAO.findAuthenticationStateByToken(token);
        if (authenticationState == null) {
            throw new RuntimeException("Invalid token.");
        }

        return mobileIDSOAPService.isAuthenticated(authenticationState);
    }

    private AuthenticationState saveResponseToAuthenticationState(
            MobileAuthenticateResponse mobileAuthenticateResponse) {
        AuthenticationState authenticationState = new AuthenticationState();
        String token = new BigInteger(130, random).toString(32);
        authenticationState.setToken(token);
        authenticationState.setCreated(new DateTime());
        authenticationState.setSessionCode(mobileAuthenticateResponse.getSessionCode());
        authenticationState.setIdCode(mobileAuthenticateResponse.getIdCode());
        authenticationState.setName(mobileAuthenticateResponse.getName());
        authenticationState.setSurname(mobileAuthenticateResponse.getSurname());
        return authenticationStateDAO.createAuthenticationState(authenticationState);
    }

    /**
     * Response sent to the front-end.
     */
    public static class MobileAuthResponse {

        /**
         * Code that is shown on the user's mobile and on the site.
         */
        private String challengeId;

        /**
         * Token that references an AuthenticationState.
         */
        private String token;

        public String getChallengeId() {
            return challengeId;
        }

        public void setChallengeId(String challengeId) {
            this.challengeId = challengeId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

    }

}
