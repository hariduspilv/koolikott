package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_MESSAGE_TO_DISPLAY;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_SERVICENAME;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;

import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.mobileid.MobileAuthResponse;

public class MobileIDLoginService {

    protected static final String MOBILE_AUTHENTICATE_MESSAGING_MODE = "asynchClientServer";
    protected static final String AUTHENTICATION_COMPLETE = "USER_AUTHENTICATED";
    protected static final String AUTHENTICATION_IN_PROGRESS = "OUTSTANDING_TRANSACTION";

    private int pollingIntervalInMilliseconds = 5000;
    private int threadLifespanInMilliseconds = 5 * 60 * 1000;

    @Inject
    private MobileIDSOAPService mobileIDSOAPService;

    @Inject
    private LanguageService languageService;

    @Inject
    private AuthenticationStateDAO authenticationStateDAO;

    @Inject
    private Configuration configuration;

    private SecureRandom random = new SecureRandom();

    public MobileAuthResponse authenticate(String phoneNumber, String idCode, Language language) throws Exception {
        if (language == null) {
            language = languageService.getLanguage("eng");
        }

        // Mobile-ID supports only these four languages
        List<String> supportedLanguages = Arrays.asList("est", "eng", "rus", "lit");
        if (!supportedLanguages.contains(language.getCode())) {
            language = languageService.getLanguage("eng");
        }

        Map<String, String> childElements = new HashMap<>();
        childElements.put("IDCode", idCode);
        childElements.put("PhoneNo", phoneNumber);
        childElements.put("Language", language.getCode().toUpperCase());
        childElements.put("ServiceName", configuration.getString(MOBILEID_SERVICENAME));
        childElements.put("MessagingMode", MOBILE_AUTHENTICATE_MESSAGING_MODE);
        String messageToDisplay = configuration.getString(MOBILEID_MESSAGE_TO_DISPLAY);
        if (!messageToDisplay.isEmpty()) {
            childElements.put("MessageToDisplay", messageToDisplay);
        }
        SOAPMessage message = mobileIDSOAPService.createSOAPMessage("MobileAuthenticate", childElements);

        SOAPMessage response = mobileIDSOAPService.sendSOAPMessage(message);

        Map<String, String> mobileAuthenticateResponse = mobileIDSOAPService.parseSOAPResponse(response);

        if (!mobileAuthenticateResponse.keySet()
                .containsAll(Arrays.asList("Sesscode", "UserIDCode", "UserGivenname", "UserSurname", "ChallengeID"))) {
            throw new RuntimeException("MobileAuthenticate response is missing one or more required fields.");
        }

        AuthenticationState authenticationState = saveResponseToAuthenticationState(mobileAuthenticateResponse);

        MobileAuthResponse mobileAuthResponse = new MobileAuthResponse();
        mobileAuthResponse.setChallengeId(mobileAuthenticateResponse.get("ChallengeID"));
        mobileAuthResponse.setToken(authenticationState.getToken());
        return mobileAuthResponse;
    }

    private AuthenticationState saveResponseToAuthenticationState(Map<String, String> mobileAuthenticateResponse) {
        AuthenticationState authenticationState = new AuthenticationState();
        String token = new BigInteger(130, random).toString(32);
        authenticationState.setToken(token);
        authenticationState.setCreated(new DateTime());
        authenticationState.setSessionCode(mobileAuthenticateResponse.get("Sesscode"));
        authenticationState.setIdCode(mobileAuthenticateResponse.get("UserIDCode"));
        authenticationState.setName(mobileAuthenticateResponse.get("UserGivenname"));
        authenticationState.setSurname(mobileAuthenticateResponse.get("UserSurname"));
        return authenticationStateDAO.createAuthenticationState(authenticationState);
    }

    public boolean isAuthenticated(String token) throws SOAPException {
        String status = AUTHENTICATION_IN_PROGRESS;
        long startTime = System.nanoTime();

        try {
            while (status.equals(AUTHENTICATION_IN_PROGRESS)) {
                status = getAuthenticationStatus(token);

                if (status.equals(AUTHENTICATION_COMPLETE)) {
                    return true;
                }

                long elapsedTime = (System.nanoTime() - startTime) / 1000000;
                if (elapsedTime < getThreadLifespan()) {
                    Thread.sleep(getPollingInterval());
                } else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return false;
    }

    private String getAuthenticationStatus(String token) throws SOAPException {
        AuthenticationState authenticationState = authenticationStateDAO.findAuthenticationStateByToken(token);
        if (authenticationState == null) {
            throw new RuntimeException("Invalid token.");
        }

        Map<String, String> childElements = new HashMap<>();
        childElements.put("Sesscode", authenticationState.getSessionCode());
        childElements.put("WaitSignature", "FALSE");
        SOAPMessage message = mobileIDSOAPService.createSOAPMessage("GetMobileAuthenticateStatus", childElements);

        SOAPMessage response = mobileIDSOAPService.sendSOAPMessage(message);

        Map<String, String> statusResponse = mobileIDSOAPService.parseSOAPResponse(response);

        return statusResponse.get("Status");
    }

    /**
     * Protected access modifier for testing purposes
     */
    protected int getPollingInterval() {
        return pollingIntervalInMilliseconds;
    }

    /**
     * Protected access modifier for testing purposes
     */
    protected int getThreadLifespan() {
        return threadLifespanInMilliseconds;
    }

}
