package ee.hm.dop.service;

import static ee.hm.dop.service.MobileIDSOAPService.AUTHENTICATION_COMPLETE;
import static ee.hm.dop.service.MobileIDSOAPService.AUTHENTICATION_IN_PROGRESS;
import static ee.hm.dop.service.MobileIDSOAPService.MOBILE_AUTHENTICATE_MESSAGING_MODE;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.configuration.Configuration;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.mobileid.soap.MobileAuthenticateResponse;

@RunWith(EasyMockRunner.class)
public class MobileIDSOAPServiceTest {

    @TestSubject
    private MobileIDSOAPService mobileIDSOAPService = new MobileIDSOAPServicePartialMock();

    @Mock
    private LanguageService languageService;

    @Mock
    private Configuration configuration;

    @Mock
    private SOAPConnection connection;

    @Test
    public void authenticate() throws Exception {
        String phoneNumber = "+37255550000";
        String idCode = "55882128025";
        Language language = new Language();
        language.setCode("rus");
        String serviceName = "ServiceNameHere";
        String messageToDisplay = "Special message";
        String endpoint = "https://www.example.com:9876/Service";

        Capture<SOAPMessage> capturedRequest = newCapture();

        Map<String, String> response = new HashMap<>();
        response.put("Sesscode", "1705273522");
        response.put("Status", "OK");
        response.put("UserIDCode", idCode);
        response.put("UserGivenname", "Richard");
        response.put("UserSurname", "Smith");
        response.put("UserCountry", "EE");
        response.put("UserCN", "RICHARD,SMITH,55882128025");
        response.put("ChallengeID", "6723");

        SOAPMessage responseMessage = createMobileAuthenticateResponse(response);

        expectConfiguration(serviceName, messageToDisplay, endpoint);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(endpoint))).andReturn(responseMessage);

        replayAll();

        MobileAuthenticateResponse mobileAuthenticateResponse = mobileIDSOAPService.authenticate(phoneNumber, idCode,
                language);

        verifyAll();

        validateMobileAuthenticateResponse(response, mobileAuthenticateResponse);

        // Validate captured request message
        Map<String, String> request = parseMessage(capturedRequest.getValue(), "MobileAuthenticate");

        assertEquals(6, request.size());
        assertEquals(idCode, request.get("IDCode"));
        assertEquals(phoneNumber, request.get("PhoneNo"));
        assertEquals(language.getCode().toUpperCase(), request.get("Language"));
        assertEquals(serviceName, request.get("ServiceName"));
        assertEquals(MOBILE_AUTHENTICATE_MESSAGING_MODE, request.get("MessagingMode"));
        assertEquals(messageToDisplay, request.get("MessageToDisplay"));
    }

    @Test
    public void authenticateNotSupportedLanguageAndMessageEmpty() throws Exception {
        String phoneNumber = "+37255550000";
        String idCode = "55882128025";
        Language language = new Language();
        language.setCode("www");
        String serviceName = "ServiceNameHere";
        String messageToDisplay = "";
        String endpoint = "https://www.example.com:9876/Service";

        Capture<SOAPMessage> capturedRequest = newCapture();

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
        languageEnglish.setCode("est");
        expect(languageService.getLanguage("est")).andReturn(languageEnglish);

        SOAPMessage responseMessage = createMobileAuthenticateResponse(response);

        expectConfiguration(serviceName, messageToDisplay, endpoint);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(endpoint))).andReturn(responseMessage);

        replayAll();

        MobileAuthenticateResponse mobileAuthenticateResponse = mobileIDSOAPService.authenticate(phoneNumber, idCode,
                language);

        verifyAll();

        validateMobileAuthenticateResponse(response, mobileAuthenticateResponse);

        // Validate captured request message
        Map<String, String> request = parseMessage(capturedRequest.getValue(), "MobileAuthenticate");
        assertEquals(5, request.size());
        assertEquals(idCode, request.get("IDCode"));
        assertEquals(phoneNumber, request.get("PhoneNo"));
        assertEquals(languageEnglish.getCode().toUpperCase(), request.get("Language"));
        assertEquals(serviceName, request.get("ServiceName"));
        assertEquals(MOBILE_AUTHENTICATE_MESSAGING_MODE, request.get("MessagingMode"));
    }

    @Test
    public void authenticateResponseMissingFields() throws Exception {
        String phoneNumber = "+37255550000";
        String idCode = "55882128025";
        Language language = new Language();
        language.setCode("rus");
        String serviceName = "ServiceNameHere";
        String messageToDisplay = "Special message";
        String endpoint = "https://www.example.com:9876/Service";

        Capture<SOAPMessage> capturedRequest = newCapture();

        Map<String, String> response = new HashMap<>();
        response.put("Sesscode", "123");

        String message = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
                + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " //
                + "xmlns:dig=\"http://www.example.com/Service/Service.wsdl\" " //
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " //
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" //
                + "<dig:MobileAuthenticateResponse>" //
                + "<Sesscode xsi:type=\"xsd:int\">" + response.get("Sesscode") + "</Sesscode>" //
                + "</dig:MobileAuthenticateResponse>" //
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
        InputStream is = new ByteArrayInputStream(message.getBytes());
        SOAPMessage responseMessage = MessageFactory.newInstance().createMessage(null, is);

        expectConfiguration(serviceName, messageToDisplay, endpoint);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(endpoint))).andReturn(responseMessage);

        replayAll();

        MobileAuthenticateResponse mobileAuthenticateResponse = null;
        try {
            mobileAuthenticateResponse = mobileIDSOAPService.authenticate(phoneNumber, idCode, language);
        } catch (RuntimeException e) {
            assertEquals("MobileAuthenticate response is missing one or more required fields.", e.getMessage());
        }

        verifyAll();

        assertNull(mobileAuthenticateResponse);
    }

    @Test
    public void authenticateNonEstonianPhoneNumber() throws Exception {
        String phoneNumber = "+37012345678";
        String idCode = "77799901010";
        Language language = new Language();
        language.setCode("rus");

        replayAll();

        MobileAuthenticateResponse mobileAuthenticateResponse = null;
        try {
            mobileAuthenticateResponse = mobileIDSOAPService.authenticate(phoneNumber, idCode, language);
        } catch (RuntimeException e) {
            assertEquals("Non-Estonian mobile numbers are not allowed.", e.getMessage());
        }

        verifyAll();

        assertNull(mobileAuthenticateResponse);
    }

    @Test
    public void authenticateFault() throws Exception {
        String phoneNumber = "+37255550000";
        String idCode = "55882128025";
        Language language = new Language();
        language.setCode("rus");
        String serviceName = "ServiceNameHere";
        String messageToDisplay = "Special message";
        String endpoint = "https://www.example.com:9876/Service";

        Capture<SOAPMessage> capturedRequest = newCapture();

        String message = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body>" //
                + "<SOAP-ENV:Fault>" //
                + "<faultcode>SOAP-ENV:Client</faultcode>" //
                + "<faultstring xml:lang=\"en\">301</faultstring>" //
                + "<detail><message>User is not a Mobile-ID client</message></detail>" //
                + "</SOAP-ENV:Fault>" //
                + "</SOAP-ENV:Body>" //
                + "</SOAP-ENV:Envelope>";
        InputStream is = new ByteArrayInputStream(message.getBytes());
        SOAPMessage responseMessage = MessageFactory.newInstance().createMessage(null, is);

        expectConfiguration(serviceName, messageToDisplay, endpoint);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(endpoint))).andReturn(responseMessage);

        replayAll();

        MobileAuthenticateResponse mobileAuthenticateResponse = null;
        try {
            mobileAuthenticateResponse = mobileIDSOAPService.authenticate(phoneNumber, idCode, language);
        } catch (RuntimeException e) {
            assertEquals("SOAPResponse Fault 301: User is not a Mobile-ID client", e.getMessage());
        }

        verifyAll();

        assertNull(mobileAuthenticateResponse);
    }

    @Test
    public void isAuthenticatedNotValid() throws Exception {
        String sessionCode = "testingSessionCode123";
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode(sessionCode);

        String endpoint = "https://www.example.com:9876/Service";

        Capture<SOAPMessage> capturedRequest = newCapture();

        Map<String, String> response = new HashMap<>();
        response.put("Status", "NOT_VALID");

        SOAPMessage responseMessage = createGetMobileAuthenticateStatusResponse(response);

        expect(configuration.getString(MOBILEID_NAMESPACE_PREFIX)).andReturn("prefix");
        expect(configuration.getString(MOBILEID_NAMESPACE_URI))
                .andReturn("http://www.example.com/Service/Service.wsdl");

        expect(configuration.getString(MOBILEID_ENDPOINT)).andReturn(endpoint);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(endpoint))).andReturn(responseMessage);

        replayAll();

        boolean isAuthenticated = mobileIDSOAPService.isAuthenticated(authenticationState);

        verifyAll();

        assertFalse(isAuthenticated);

        // Validate captured request message
        Map<String, String> request = parseMessage(capturedRequest.getValue(), "GetMobileAuthenticateStatus");
        assertEquals(2, request.size());
        assertEquals(sessionCode, request.get("Sesscode"));
        assertEquals("FALSE", request.get("WaitSignature"));
    }

    @Test
    public void isAuthenticated() throws Exception {
        String sessionCode = "testingSessionCode123";
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode(sessionCode);

        String endpoint = "https://www.example.com:9876/Service";

        Capture<SOAPMessage> capturedRequest = newCapture();

        Map<String, String> firstResponse = new HashMap<>();
        firstResponse.put("Status", AUTHENTICATION_IN_PROGRESS);
        SOAPMessage firstResponseMessage = createGetMobileAuthenticateStatusResponse(firstResponse);

        Map<String, String> secondResponse = new HashMap<>();
        secondResponse.put("Status", AUTHENTICATION_COMPLETE);
        SOAPMessage secondResponseMessage = createGetMobileAuthenticateStatusResponse(secondResponse);

        // Set SOAP expects for 2 SOAP requests
        expect(configuration.getString(MOBILEID_NAMESPACE_PREFIX)).andReturn("prefix").times(2);
        expect(configuration.getString(MOBILEID_NAMESPACE_URI)).andReturn("http://www.example.com/Service/Service.wsdl")
                .times(2);

        expect(configuration.getString(MOBILEID_ENDPOINT)).andReturn(endpoint).times(2);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(endpoint)))
                .andReturn(firstResponseMessage);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(endpoint)))
                .andReturn(secondResponseMessage);

        replayAll();

        boolean isAuthenticated = mobileIDSOAPService.isAuthenticated(authenticationState);

        verifyAll();

        assertTrue(isAuthenticated);

        // Validate captured request message
        Map<String, String> request = parseMessage(capturedRequest.getValue(), "GetMobileAuthenticateStatus");
        assertEquals(2, request.size());
        assertEquals(sessionCode, request.get("Sesscode"));
        assertEquals("FALSE", request.get("WaitSignature"));
    }

    @Test
    public void isAuthenticatedResponseMissingFields() throws Exception {
        String sessionCode = "testingSessionCode123";
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode(sessionCode);

        String endpoint = "https://www.example.com:9876/Service";

        Capture<SOAPMessage> capturedRequest = newCapture();

        String message = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
                + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " //
                + "xmlns:dig=\"http://www.example.com/Service/Service.wsdl\" " //
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " //
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" //
                + "<dig:MobileAuthenticateResponse>" //
                + "</dig:MobileAuthenticateResponse>" //
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
        InputStream is = new ByteArrayInputStream(message.getBytes());
        SOAPMessage responseMessage = MessageFactory.newInstance().createMessage(null, is);

        expect(configuration.getString(MOBILEID_NAMESPACE_PREFIX)).andReturn("prefix");
        expect(configuration.getString(MOBILEID_NAMESPACE_URI))
                .andReturn("http://www.example.com/Service/Service.wsdl");

        expect(configuration.getString(MOBILEID_ENDPOINT)).andReturn(endpoint);
        expect(connection.call(EasyMock.capture(capturedRequest), EasyMock.eq(endpoint))).andReturn(responseMessage);

        replayAll();

        boolean isAuthenticated = false;
        try {
            isAuthenticated = mobileIDSOAPService.isAuthenticated(authenticationState);
        } catch (RuntimeException e) {
            assertEquals("GetMobileAuthenticateStatusResponse response is missing a Status field.", e.getMessage());
        }
        verifyAll();

        assertFalse(isAuthenticated);

        // Validate captured request message
        Map<String, String> request = parseMessage(capturedRequest.getValue(), "GetMobileAuthenticateStatus");
        assertEquals(2, request.size());
        assertEquals(sessionCode, request.get("Sesscode"));
        assertEquals("FALSE", request.get("WaitSignature"));
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
        assertEquals(expectedResponse.get("Sesscode"), mobileAuthenticateResponse.getSessionCode());
        assertEquals(expectedResponse.get("Status"), mobileAuthenticateResponse.getStatus());
        assertEquals(expectedResponse.get("UserIDCode"), mobileAuthenticateResponse.getIdCode());
        assertEquals(expectedResponse.get("UserGivenname"), mobileAuthenticateResponse.getName());
        assertEquals(expectedResponse.get("UserSurname"), mobileAuthenticateResponse.getSurname());
        assertEquals(expectedResponse.get("UserCountry"), mobileAuthenticateResponse.getCountry());
        assertEquals(expectedResponse.get("UserCN"), mobileAuthenticateResponse.getUserCommonName());
        assertEquals(expectedResponse.get("ChallengeID"), mobileAuthenticateResponse.getChallengeID());
    }

    private SOAPMessage createMobileAuthenticateResponse(Map<String, String> response) throws Exception {
        String message = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
                + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " //
                + "xmlns:dig=\"http://www.example.com/Service/Service.wsdl\" " //
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " //
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" //
                + "<dig:MobileAuthenticateResponse>" //
                + "<Sesscode xsi:type=\"xsd:int\">" + response.get("Sesscode") + "</Sesscode>" //
                + "<Status xsi:type=\"xsd:string\">" + response.get("Status") + "</Status>" //
                + "<UserIDCode xsi:type=\"xsd:string\">" + response.get("UserIDCode") + "</UserIDCode>" //
                + "<UserGivenname xsi:type=\"xsd:string\">" + response.get("UserGivenname") + "</UserGivenname>" //
                + "<UserSurname xsi:type=\"xsd:string\">" + response.get("UserSurname") + "</UserSurname>" //
                + "<UserCountry xsi:type=\"xsd:string\">" + response.get("UserCountry") + "</UserCountry>" //
                + "<UserCN xsi:type=\"xsd:string\">" + response.get("UserCN") + "</UserCN>" //
                + "<ChallengeID xsi:type=\"xsd:string\">" + response.get("ChallengeID") + "</ChallengeID>" //
                + "</dig:MobileAuthenticateResponse>" //
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
        InputStream is = new ByteArrayInputStream(message.getBytes());
        return MessageFactory.newInstance().createMessage(null, is);
    }

    private SOAPMessage createGetMobileAuthenticateStatusResponse(Map<String, String> response) throws Exception {
        String message = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
                + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " //
                + "xmlns:dig=\"http://www.example.com/Service/Service.wsdl\" " //
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " //
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" //
                + "<dig:GetMobileAuthenticateStatusResponse>" //
                + "<Status xsi:type=\"xsd:string\">" + response.get("Status") + "</Status>" //
                + "<Signature xsi:type=\"xsd:string\"/>" //
                + "</dig:GetMobileAuthenticateStatusResponse>" //
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
        InputStream is = new ByteArrayInputStream(message.getBytes());
        return MessageFactory.newInstance().createMessage(null, is);
    }

    private void expectConfiguration(String serviceName, String messageToDisplay, String endpoint) {
        expect(configuration.getString(MOBILEID_SERVICENAME)).andReturn(serviceName);
        expect(configuration.getString(MOBILEID_MESSAGE_TO_DISPLAY)).andReturn(messageToDisplay);

        expect(configuration.getString(MOBILEID_NAMESPACE_PREFIX)).andReturn("prefix");
        expect(configuration.getString(MOBILEID_NAMESPACE_URI))
                .andReturn("http://www.example.com/Service/Service.wsdl");

        expect(configuration.getString(MOBILEID_ENDPOINT)).andReturn(endpoint);
    }

    private class MobileIDSOAPServicePartialMock extends MobileIDSOAPService {
        @Override
        protected int getPollingInterval() {
            return 100;
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

}
