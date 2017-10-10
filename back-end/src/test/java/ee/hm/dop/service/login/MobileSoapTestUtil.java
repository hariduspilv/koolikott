package ee.hm.dop.service.login;

import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.enums.LanguageC;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MobileSoapTestUtil {
    public static final String OK = "OK";
    public static final String NOK = "SOMETHING_WENT_WRONG";
    public static final Language LANGUAGE_RUS = language(LanguageC.RUS);
    public static final String SESSION_CODE = "testingSessionCode123";
    public static final String SESSCODE = "Sesscode";
    public static final Language LANGUAGE_EST = language(LanguageC.EST);
    public static final String MOBILE_ID_ENDPOINT = "https://www.example.com:9876/Service";
    public static final String PHONE_NUMBER = "+37255550000";
    public static final String ID_CODE = "55882128025";
    public static final String SERVICE_NAME = "ServiceNameHere";
    public static final String MESSAGE = "Special message";
    public static final String WWW = "www";
    public static final Language LANGUAGE_WWW = language(WWW);
    public static final String EMPTY_MESSAGE = "";

    public static Language language(String code) {
        Language language = new Language();
        language.setCode(code);
        return language;
    }

    public static String missingFields(Map<String, String> response) {
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
                + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " //
                + "xmlns:dig=\"http://www.example.com/Service/Service.wsdl\" " //
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " //
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" //
                + "<dig:MobileAuthenticateResponse>" //
                + "<Sesscode xsi:type=\"xsd:int\">" + response.get(SESSCODE) + "</Sesscode>" //
                + "</dig:MobileAuthenticateResponse>" //
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

    public static String faultMessage() {
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body>" //
                + "<SOAP-ENV:Fault>" //
                + "<faultcode>SOAP-ENV:Client</faultcode>" //
                + "<faultstring xml:lang=\"en\">301</faultstring>" //
                + "<detail><message>User is not a Mobile-ID client</message></detail>" //
                + "</SOAP-ENV:Fault>" //
                + "</SOAP-ENV:Body>" //
                + "</SOAP-ENV:Envelope>";
    }

    public static SOAPMessage createMessage(String message) throws IOException, SOAPException {
        InputStream is = new ByteArrayInputStream(message.getBytes());
        return MessageFactory.newInstance().createMessage(null, is);
    }

    public static Map<String, String> map(String key, String value) {
        Map<String, String> response = new HashMap<>();
        response.put(key, value);
        return response;
    }

    public static AuthenticationState authentication() {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode(SESSION_CODE);
        return authenticationState;
    }

    public static String authMessage2(Map<String, String> response) throws Exception {
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
                + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " //
                + "xmlns:dig=\"http://www.example.com/Service/Service.wsdl\" " //
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " //
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" //
                + "<dig:MobileAuthenticateResponse>" //
                + "<Sesscode xsi:type=\"xsd:int\">" + response.get(SESSCODE) + "</Sesscode>" //
                + "<Status xsi:type=\"xsd:string\">" + response.get("Status") + "</Status>" //
                + "<UserIDCode xsi:type=\"xsd:string\">" + response.get("UserIDCode") + "</UserIDCode>" //
                + "<UserGivenname xsi:type=\"xsd:string\">" + response.get("UserGivenname") + "</UserGivenname>" //
                + "<UserSurname xsi:type=\"xsd:string\">" + response.get("UserSurname") + "</UserSurname>" //
                + "<UserCountry xsi:type=\"xsd:string\">" + response.get("UserCountry") + "</UserCountry>" //
                + "<UserCN xsi:type=\"xsd:string\">" + response.get("UserCN") + "</UserCN>" //
                + "<ChallengeID xsi:type=\"xsd:string\">" + response.get("ChallengeID") + "</ChallengeID>" //
                + "</dig:MobileAuthenticateResponse>" //
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

    public static String authMessage1(Map<String, String> response) throws Exception {
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
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
    }

    public static String message() {
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " //
                + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " //
                + "xmlns:dig=\"http://www.example.com/Service/Service.wsdl\" " //
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " //
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" //
                + "<SOAP-ENV:Header/>" //
                + "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" //
                + "<dig:MobileAuthenticateResponse>" //
                + "</dig:MobileAuthenticateResponse>" //
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

    public static Map<String, String> response(String idCode, String status) {
        Map<String, String> response = new HashMap<>();
        response.put(SESSCODE, "1705273522");
        response.put("Status", status);
        response.put("UserIDCode", idCode);
        response.put("UserGivenname", "Richard");
        response.put("UserSurname", "Smith");
        response.put("UserCountry", "EE");
        response.put("UserCN", "RICHARD,SMITH,55882128025");
        response.put("ChallengeID", "6723");
        return response;
    }
}
