package ee.hm.dop.service.login;

import static ee.hm.dop.service.login.MobileIDSOAPService.AUTHENTICATION_COMPLETE;
import static ee.hm.dop.service.login.MobileIDSOAPService.AUTHENTICATION_IN_PROGRESS;
import static ee.hm.dop.service.login.MobileIDSOAPService.MOBILE_AUTHENTICATE_MESSAGING_MODE;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_ENDPOINT;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_MESSAGE_TO_DISPLAY;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_NAMESPACE_PREFIX;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_NAMESPACE_URI;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_SERVICENAME;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.mobileid.soap.MobileAuthenticateResponse;
import ee.hm.dop.service.metadata.LanguageService;
import org.apache.commons.configuration.Configuration;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class MobileIDSOAPServiceTest extends MobileSoapTestUtil {

    @TestSubject
    private MobileIDSOAPServicePartialMock mobileIDSOAPService = new MobileIDSOAPServicePartialMock();
    @Mock
    private LanguageService languageService;
    @Mock
    private Configuration configuration;
    @Mock
    private SOAPConnection connection;

    @Test
    public void user_can_authenticate() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();
        Map<String, String> response = response(ID_CODE, OK);
        SOAPMessage responseMessage = createMessage(authMessage2(response));

        expectConfiguration(SERVICE_NAME, MESSAGE, MOBILE_ID_ENDPOINT);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(responseMessage);

        replayAll();

        MobileAuthenticateResponse authResponse = authenticate(LANGUAGE_RUS);

        verifyAll();

        validateMobileAuthenticateResponse(response, authResponse);
        validateSuccess(capturedRequest, LANGUAGE_RUS);
    }

    @Test
    public void authenticateNotSupportedLanguageAndMessageEmpty() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();
        Map<String, String> response = response(ID_CODE, OK);
        expect(languageService.getLanguage(LanguageC.EST)).andReturn(LANGUAGE_EST);

        SOAPMessage responseMessage = createMessage(authMessage2(response));

        expectConfiguration(SERVICE_NAME, EMPTY_MESSAGE, MOBILE_ID_ENDPOINT);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(responseMessage);

        replayAll();

        MobileAuthenticateResponse mobileAuthenticateResponse = authenticate(LANGUAGE_WWW);

        verifyAll();

        validateMobileAuthenticateResponse(response, mobileAuthenticateResponse);
        validateSuccessNoMessage(capturedRequest);
    }

    @Test
    public void authenticateNullLanguage() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();

        expect(languageService.getLanguage(LanguageC.EST)).andReturn(LANGUAGE_EST);
        expectConfiguration(SERVICE_NAME, MESSAGE, MOBILE_ID_ENDPOINT);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(
                createMessage(authMessage2(response(ID_CODE, OK))));

        replayAll();

        MobileAuthenticateResponse mobileAuthenticateResponse = authenticate(null);

        verifyAll();

        validateMobileAuthenticateResponse(response(ID_CODE, OK), mobileAuthenticateResponse);
        validateSuccess(capturedRequest, LANGUAGE_EST);
    }

    @Test
    public void authentication_fails_when_fields_are_missing() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();

        expectConfiguration(SERVICE_NAME, MESSAGE, MOBILE_ID_ENDPOINT);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(
                createMessage(missingFields(map(SESSCODE, "123"))));

        replayAll();

        MobileAuthenticateResponse response = authenticate(LANGUAGE_RUS);

        verifyAll();

        assertNull(response);
    }

    @Test
    public void authentication_fails() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();

        expectConfiguration(SERVICE_NAME, MESSAGE, MOBILE_ID_ENDPOINT);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(
                createMessage(faultMessage()));

        replayAll();

        MobileAuthenticateResponse response = authenticate(LANGUAGE_RUS);

        verifyAll();

        assertNull(response);
    }

    @Test
    public void authentication_is_NOK() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();

        expectConfiguration(SERVICE_NAME, MESSAGE, MOBILE_ID_ENDPOINT);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(
                createMessage(authMessage2(response(ID_CODE, NOK))));

        replayAll();

        MobileAuthenticateResponse mobileAuthenticateResponse = authenticate(LANGUAGE_RUS);

        verifyAll();

        assertNull(mobileAuthenticateResponse);
    }

    @Test
    public void user_is_not_authenticated() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();
        SOAPMessage responseMessage = createMessage(authMessage1(map("Status", "NOT_VALID")));

        soapExpectations(capturedRequest, responseMessage);

        replayAll();

        boolean isAuthenticated = mobileIDSOAPService.isAuthenticated(authentication());

        verifyAll();

        assertFalse(isAuthenticated);
        validateIsAuthenticated(capturedRequest);
    }

    @Test
    public void user_is_authenticated() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();

        SOAPMessage firstResponseMessage = createMessage(authMessage1(map("Status", AUTHENTICATION_IN_PROGRESS)));
        SOAPMessage secondResponseMessage = createMessage(authMessage1(map("Status", AUTHENTICATION_COMPLETE)));

        soapExpectations(capturedRequest, firstResponseMessage, secondResponseMessage);

        replayAll();

        boolean isAuthenticated = mobileIDSOAPService.isAuthenticated(authentication());

        verifyAll();

        assertTrue(isAuthenticated);
        validateIsAuthenticated(capturedRequest);
    }

    @Test
    public void user_is_not_authenticated_if_fields_are_not_filled() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();
        soapExpectations(capturedRequest, createMessage(message()));

        replayAll();

        boolean isAuthenticated = mobileIDSOAPService.isAuthenticated(authentication());

        verifyAll();

        assertFalse(isAuthenticated);
        validateIsAuthenticated(capturedRequest);
    }

    @Test
    public void user_is_not_authenticated_if_authentication_is_interrupted_while_authenticating() throws Exception {
        Capture<SOAPMessage> capturedRequest = newCapture();
        soapExpectations(capturedRequest, createMessage(authMessage1(map("Status", AUTHENTICATION_IN_PROGRESS))));
        mobileIDSOAPService.setShouldThrowException(true);

        replayAll();

        boolean isAuthenticated = mobileIDSOAPService.isAuthenticated(authentication());

        verifyAll();

        assertFalse(isAuthenticated);
    }

    private Map<String, String> parseMessage(SOAPMessage message, String messageName) throws SOAPException {
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();
        SOAPElement messageElement = (SOAPElement) body.getChildElements().next();
        assertEquals(messageName, messageElement.getElementName().getLocalName());

        @SuppressWarnings("unchecked")
        Iterator<SOAPElement> iterator = messageElement.getChildElements();
        Map<String, String> response = new HashMap<>();
        while (iterator.hasNext()) {
            SOAPElement element = iterator.next();
            response.put(element.getElementName().getLocalName(), element.getValue());
        }
        return response;
    }

    private void validateMobileAuthenticateResponse(Map<String, String> expectedResponse,
                                                    MobileAuthenticateResponse mobileAuthenticateResponse) {
        assertEquals(expectedResponse.get(SESSCODE), mobileAuthenticateResponse.getSessionCode());
        assertEquals(expectedResponse.get("Status"), mobileAuthenticateResponse.getStatus());
        assertEquals(expectedResponse.get("UserIDCode"), mobileAuthenticateResponse.getIdCode());
        assertEquals(expectedResponse.get("UserGivenname"), mobileAuthenticateResponse.getName());
        assertEquals(expectedResponse.get("UserSurname"), mobileAuthenticateResponse.getSurname());
        assertEquals(expectedResponse.get("UserCountry"), mobileAuthenticateResponse.getCountry());
        assertEquals(expectedResponse.get("UserCN"), mobileAuthenticateResponse.getUserCommonName());
        assertEquals(expectedResponse.get("ChallengeID"), mobileAuthenticateResponse.getChallengeID());
    }

    private void expectConfiguration(String serviceName, String messageToDisplay, String endpoint) {
        expect(configuration.getString(MOBILEID_SERVICENAME)).andReturn(serviceName);
        expect(configuration.getString(MOBILEID_MESSAGE_TO_DISPLAY)).andReturn(messageToDisplay);

        expect(configuration.getString(MOBILEID_NAMESPACE_PREFIX)).andReturn("prefix");
        expect(configuration.getString(MOBILEID_NAMESPACE_URI))
                .andReturn("http://www.example.com/Service/Service.wsdl");

        expect(configuration.getString(MOBILEID_ENDPOINT)).andReturn(endpoint);
    }

    private void soapExpectations(Capture<SOAPMessage> capturedRequest, SOAPMessage firstResponseMessage, SOAPMessage secondResponseMessage) throws SOAPException {
        expect(configuration.getString(MOBILEID_NAMESPACE_PREFIX)).andReturn("prefix").times(2);
        expect(configuration.getString(MOBILEID_NAMESPACE_URI))
                .andReturn("http://www.example.com/Service/Service.wsdl").times(2);

        expect(configuration.getString(MOBILEID_ENDPOINT)).andReturn(MOBILE_ID_ENDPOINT).times(2);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(
                firstResponseMessage);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(
                secondResponseMessage);
    }

    private class MobileIDSOAPServicePartialMock extends MobileIDSOAPService {
        private boolean shouldThrowException = false;

        @Override
        protected int getPollingInterval() {
            if (shouldThrowException) {
                shouldThrowException = false;
                Thread.currentThread().interrupt();
            }
            return 100;
        }

        public void setShouldThrowException(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }
    }

    private void replayAll(Object... mocks) {
        replay(languageService, configuration, connection);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(languageService, configuration, connection);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

    private void validateSuccess(Capture<SOAPMessage> capturedRequest, Language languageRus) throws SOAPException {
        Map<String, String> request = parseMessage(capturedRequest.getValue(), "MobileAuthenticate");
        assertEquals(6, request.size());
        assertEquals(ID_CODE, request.get("IDCode"));
        assertEquals(PHONE_NUMBER, request.get("PhoneNo"));
        assertEquals(languageRus.getCode().toUpperCase(), request.get("Language"));
        assertEquals(SERVICE_NAME, request.get("ServiceName"));
        assertEquals(MOBILE_AUTHENTICATE_MESSAGING_MODE, request.get("MessagingMode"));
        assertEquals(MESSAGE, request.get("MessageToDisplay"));
    }

    private void validateSuccessNoMessage(Capture<SOAPMessage> capturedRequest) throws SOAPException {
        Map<String, String> request = parseMessage(capturedRequest.getValue(), "MobileAuthenticate");
        assertEquals(5, request.size());
        assertEquals(ID_CODE, request.get("IDCode"));
        assertEquals(PHONE_NUMBER, request.get("PhoneNo"));
        assertEquals(LANGUAGE_EST.getCode().toUpperCase(), request.get("Language"));
        assertEquals(SERVICE_NAME, request.get("ServiceName"));
        assertEquals(MOBILE_AUTHENTICATE_MESSAGING_MODE, request.get("MessagingMode"));
    }

    private void soapExpectations(Capture<SOAPMessage> capturedRequest, SOAPMessage responseMessage) throws SOAPException {
        expect(configuration.getString(MOBILEID_NAMESPACE_PREFIX)).andReturn("prefix");
        expect(configuration.getString(MOBILEID_NAMESPACE_URI))
                .andReturn("http://www.example.com/Service/Service.wsdl");

        expect(configuration.getString(MOBILEID_ENDPOINT)).andReturn(MOBILE_ID_ENDPOINT);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(MOBILE_ID_ENDPOINT))).andReturn(
                responseMessage);
    }

    private void validateIsAuthenticated(Capture<SOAPMessage> capturedRequest) throws SOAPException {
        Map<String, String> request = parseMessage(capturedRequest.getValue(), "GetMobileAuthenticateStatus");
        assertEquals(2, request.size());
        assertEquals(SESSION_CODE, request.get(SESSCODE));
        assertEquals("FALSE", request.get("WaitSignature"));
    }

    private MobileAuthenticateResponse authenticate(Language languageRus) throws SOAPException {
        return mobileIDSOAPService.authenticate(PHONE_NUMBER, ID_CODE, languageRus);
    }
}
