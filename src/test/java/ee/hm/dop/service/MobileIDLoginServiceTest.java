package ee.hm.dop.service;

import static ee.hm.dop.service.MobileIDLoginService.AUTHENTICATION_COMPLETE;
import static ee.hm.dop.service.MobileIDLoginService.AUTHENTICATION_IN_PROGRESS;
import static ee.hm.dop.service.MobileIDLoginService.MOBILE_AUTHENTICATE_MESSAGING_MODE;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_MESSAGE_TO_DISPLAY;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_SERVICENAME;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.configuration.Configuration;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.IAnswer;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.mobileid.MobileAuthResponse;

@RunWith(EasyMockRunner.class)
public class MobileIDLoginServiceTest {

    @TestSubject
    private MobileIDLoginService mobileIDLoginService = new MobileIDLoginServicePartialMock();

    @Mock
    private MobileIDSOAPService mobileIDSOAPService;

    @Mock
    private LanguageService languageService;

    @Mock
    private AuthenticationStateDAO authenticationStateDAO;

    @Mock
    private Configuration configuration;

    @Test
    public void startMobileAuthentication() throws Exception {
        String phoneNumber = "55550000";
        String idCode = "55882128025";
        Language language = new Language();
        language.setCode("rus");
        String serviceName = "ServiceNameHere";
        String messageToDisplay = "Special message";

        Capture<AuthenticationState> capturedAuthenticationState = newCapture();
        Capture<Map<String, String>> capturedChildElements = newCapture();

        Map<String, String> response = new HashMap<>();
        response.put("Sesscode", "1705273522");
        response.put("Status", "OK");
        response.put("UserIDCode", idCode);
        response.put("UserGivenname", "Richard");
        response.put("UserSurname", "Smith");
        response.put("UserCountry", "EE");
        response.put("UserCN", "RICHARD,SMITH,55882128025");
        response.put("ChallengeID", "6723");

        expect(configuration.getString(MOBILEID_SERVICENAME)).andReturn(serviceName);
        expect(configuration.getString(MOBILEID_MESSAGE_TO_DISPLAY)).andReturn(messageToDisplay);

        setSOAPExpects("MobileAuthenticate", capturedChildElements, response);

        expectCreateAuthenticationState(capturedAuthenticationState);

        replayAll();

        MobileAuthResponse mobileAuthResponse = mobileIDLoginService.authenticate(phoneNumber, idCode, language);

        verifyAll();

        validateAuthenticationState(capturedAuthenticationState, response);
        validateMobileAuthResponse(mobileAuthResponse, response, capturedAuthenticationState);

        // Validate captured childElements of created message
        assertEquals(6, capturedChildElements.getValue().size());
        assertEquals(idCode, capturedChildElements.getValue().get("IDCode"));
        assertEquals(phoneNumber, capturedChildElements.getValue().get("PhoneNo"));
        assertEquals(language.getCode().toUpperCase(), capturedChildElements.getValue().get("Language"));
        assertEquals(serviceName, capturedChildElements.getValue().get("ServiceName"));
        assertEquals(MOBILE_AUTHENTICATE_MESSAGING_MODE, capturedChildElements.getValue().get("MessagingMode"));
        assertEquals(messageToDisplay, capturedChildElements.getValue().get("MessageToDisplay"));
    }

    @Test
    public void startMobileAuthenticationNotSupportedLanguageAndMessageEmpty() throws Exception {
        String phoneNumber = "55550000";
        String idCode = "55882128025";
        Language language = new Language();
        language.setCode("www");
        String serviceName = "ServiceNameHere";
        String messageToDisplay = "";

        Capture<AuthenticationState> capturedAuthenticationState = newCapture();
        Capture<Map<String, String>> capturedChildElements = newCapture();

        Map<String, String> response = new HashMap<>();
        response.put("Sesscode", "1705273522");
        response.put("Status", "OK");
        response.put("UserIDCode", idCode);
        response.put("UserGivenname", "Richard");
        response.put("UserSurname", "Smith");
        response.put("UserCountry", "EE");
        response.put("UserCN", "RICHARD,SMITH,55882128025");
        response.put("ChallengeID", "6723");

        Language languageEnglish = new Language();
        languageEnglish.setCode("eng");
        expect(languageService.getLanguage("eng")).andReturn(languageEnglish);

        expect(configuration.getString(MOBILEID_SERVICENAME)).andReturn(serviceName);
        expect(configuration.getString(MOBILEID_MESSAGE_TO_DISPLAY)).andReturn(messageToDisplay);

        setSOAPExpects("MobileAuthenticate", capturedChildElements, response);

        expectCreateAuthenticationState(capturedAuthenticationState);

        replayAll();

        MobileAuthResponse mobileAuthResponse = mobileIDLoginService.authenticate(phoneNumber, idCode, language);

        verifyAll();

        validateAuthenticationState(capturedAuthenticationState, response);
        validateMobileAuthResponse(mobileAuthResponse, response, capturedAuthenticationState);

        // Validate captured childElements of created message
        assertEquals(5, capturedChildElements.getValue().size());
        assertEquals(idCode, capturedChildElements.getValue().get("IDCode"));
        assertEquals(phoneNumber, capturedChildElements.getValue().get("PhoneNo"));
        assertEquals("ENG", capturedChildElements.getValue().get("Language"));
        assertEquals(serviceName, capturedChildElements.getValue().get("ServiceName"));
        assertEquals(MOBILE_AUTHENTICATE_MESSAGING_MODE, capturedChildElements.getValue().get("MessagingMode"));
    }

    @Test
    public void startMobileAuthenticationResponseMissingFields() throws Exception {
        String phoneNumber = "55550000";
        String idCode = "55882128025";
        Language language = new Language();
        language.setCode("rus");

        Capture<Map<String, String>> capturedChildElements = newCapture();

        Map<String, String> response = new HashMap<>();
        response.put("Sesscode", "123");

        expect(configuration.getString(MOBILEID_SERVICENAME)).andReturn("ServiceNameHere");
        expect(configuration.getString(MOBILEID_MESSAGE_TO_DISPLAY)).andReturn("Special message");

        setSOAPExpects("MobileAuthenticate", capturedChildElements, response);

        replayAll();

        MobileAuthResponse mobileAuthResponse = null;
        try {
            mobileAuthResponse = mobileIDLoginService.authenticate(phoneNumber, idCode, language);
        } catch (RuntimeException e) {
            assertEquals("MobileAuthenticate response is missing one or more required fields.", e.getMessage());
        }

        verifyAll();

        assertNull(mobileAuthResponse);
    }

    @Test
    public void isAuthenticatedInvalidToken() throws SOAPException {
        String token = "invalidTOKEN";

        expect(authenticationStateDAO.findAuthenticationStateByToken(token)).andReturn(null);

        replayAll();

        try {
            mobileIDLoginService.isAuthenticated(token);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals("Invalid token.", e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void isAuthenticatedNotValid() throws SOAPException {
        String token = "validTOKEN";
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode("testingSessionCode123");

        Capture<Map<String, String>> capturedChildElements = newCapture();

        Map<String, String> response = new HashMap<>();
        response.put("Status", "NOT_VALID");

        expect(authenticationStateDAO.findAuthenticationStateByToken(token)).andReturn(authenticationState);

        setSOAPExpects("GetMobileAuthenticateStatus", capturedChildElements, response);

        replayAll();

        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(token);

        verifyAll();

        assertFalse(isAuthenticated);
    }

    @Test
    public void isAuthenticated() throws SOAPException {
        String token = "validTOKEN";
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode("testingSessionCode123");

        Capture<Map<String, String>> capturedChildElements = newCapture();

        Map<String, String> firstResponse = new HashMap<>();
        firstResponse.put("Status", AUTHENTICATION_IN_PROGRESS);

        Map<String, String> secondResponse = new HashMap<>();
        secondResponse.put("Status", AUTHENTICATION_COMPLETE);

        expect(authenticationStateDAO.findAuthenticationStateByToken(token)).andReturn(authenticationState).times(2);

        // Set SOAP expects for 2 SOAP requests
        SOAPMessage messageMock = EasyMock.createMock(SOAPMessage.class);
        SOAPMessage responseMock = EasyMock.createMock(SOAPMessage.class);

        expect(mobileIDSOAPService.createSOAPMessage(EasyMock.eq("GetMobileAuthenticateStatus"),
                EasyMock.capture(capturedChildElements))).andReturn(messageMock).times(2);
        expect(mobileIDSOAPService.sendSOAPMessage(messageMock)).andReturn(responseMock).times(2);
        expect(mobileIDSOAPService.parseSOAPResponse(responseMock)).andReturn(firstResponse);
        expect(mobileIDSOAPService.parseSOAPResponse(responseMock)).andReturn(secondResponse);

        replayAll();

        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(token);

        verifyAll();

        assertTrue(isAuthenticated);
    }

    @Test
    public void isAuthenticatedTimeout() throws SOAPException {
        String token = "validTOKEN";
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode("testingSessionCode123");

        Capture<Map<String, String>> capturedChildElements = newCapture();

        Map<String, String> response = new HashMap<>();
        response.put("Status", AUTHENTICATION_IN_PROGRESS);

        expect(authenticationStateDAO.findAuthenticationStateByToken(token)).andReturn(authenticationState).anyTimes();

        // Set SOAP expects for many SOAP requests
        SOAPMessage messageMock = EasyMock.createMock(SOAPMessage.class);
        SOAPMessage responseMock = EasyMock.createMock(SOAPMessage.class);

        expect(mobileIDSOAPService.createSOAPMessage(EasyMock.eq("GetMobileAuthenticateStatus"),
                EasyMock.capture(capturedChildElements))).andReturn(messageMock).anyTimes();
        expect(mobileIDSOAPService.sendSOAPMessage(messageMock)).andReturn(responseMock).anyTimes();
        expect(mobileIDSOAPService.parseSOAPResponse(responseMock)).andReturn(response).anyTimes();

        replayAll();

        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(token);

        verifyAll();

        assertFalse(isAuthenticated);
    }

    private void setSOAPExpects(String messageName, Capture<Map<String, String>> capturedChildElements,
            Map<String, String> response) throws SOAPException {
        SOAPMessage messageMock = EasyMock.createMock(SOAPMessage.class);
        SOAPMessage responseMock = EasyMock.createMock(SOAPMessage.class);

        expect(mobileIDSOAPService.createSOAPMessage(EasyMock.eq(messageName), EasyMock.capture(capturedChildElements)))
                .andReturn(messageMock);
        expect(mobileIDSOAPService.sendSOAPMessage(messageMock)).andReturn(responseMock);
        expect(mobileIDSOAPService.parseSOAPResponse(responseMock)).andReturn(response);
    }

    private void expectCreateAuthenticationState(Capture<AuthenticationState> capturedAuthenticationState) {
        // Using .andReturn(capturedAuthenticationState.getValue()) would give
        // error saying "Nothing captured yet"
        expect(authenticationStateDAO.createAuthenticationState(EasyMock.capture(capturedAuthenticationState)))
                .andAnswer(new IAnswer<AuthenticationState>() {
                    @Override
                    public AuthenticationState answer() throws Throwable {
                        return capturedAuthenticationState.getValue();
                    }
                });
    }

    private void validateAuthenticationState(Capture<AuthenticationState> capturedAuthenticationState,
            Map<String, String> response) {
        assertEquals(response.get("UserIDCode"), capturedAuthenticationState.getValue().getIdCode());
        assertEquals(response.get("UserGivenname"), capturedAuthenticationState.getValue().getName());
        assertEquals(response.get("UserSurname"), capturedAuthenticationState.getValue().getSurname());
        assertEquals(response.get("Sesscode"), capturedAuthenticationState.getValue().getSessionCode());
        DateTime created = capturedAuthenticationState.getValue().getCreated();
        assertFalse(created.isAfterNow());
    }

    private void validateMobileAuthResponse(MobileAuthResponse mobileAuthResponse, Map<String, String> response,
            Capture<AuthenticationState> capturedAuthenticationState) {
        assertEquals(response.get("ChallengeID"), mobileAuthResponse.getChallengeId());
        assertNotNull(capturedAuthenticationState.getValue().getToken());
        assertEquals(capturedAuthenticationState.getValue().getToken(), mobileAuthResponse.getToken());
    }

    private class MobileIDLoginServicePartialMock extends MobileIDLoginService {
        @Override
        protected int getPollingInterval() {
            return 100;
        }

        @Override
        protected int getThreadLifespan() {
            return 5000;
        }
    }

    private void replayAll(Object... mocks) {
        replay(languageService, authenticationStateDAO, configuration, mobileIDSOAPService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(languageService, authenticationStateDAO, configuration, mobileIDSOAPService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
