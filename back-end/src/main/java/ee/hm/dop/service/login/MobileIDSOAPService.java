package ee.hm.dop.service.login;

import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_ENDPOINT;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_MESSAGE_TO_DISPLAY;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_NAMESPACE_PREFIX;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_NAMESPACE_URI;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_SERVICENAME;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.mobileid.soap.GetMobileAuthenticateStatusResponse;
import ee.hm.dop.model.mobileid.soap.MobileAuthenticateResponse;
import ee.hm.dop.service.metadata.LanguageService;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobileIDSOAPService {

    private static Logger logger = LoggerFactory.getLogger(MobileIDSOAPService.class);

    protected static final String MOBILE_AUTHENTICATE_MESSAGING_MODE = "asynchClientServer";
    protected static final String AUTHENTICATION_COMPLETE = "USER_AUTHENTICATED";
    protected static final String AUTHENTICATION_IN_PROGRESS = "OUTSTANDING_TRANSACTION";

    private static final int POLLING_INTERVAL_IN_MILLISECONDS = 5000;

    /**
     * Mobile-ID supports only these four languagesprotected
     */
    private static final Collection<String> supportedLanguages = Arrays.asList("est", "eng", "rus", "lit");

    @Inject
    private LanguageService languageService;
    @Inject
    private Configuration configuration;
    @Inject
    private SOAPConnection connection;

    public MobileAuthenticateResponse authenticate(String phoneNumber, String idCode, Language language)
            throws SOAPException {
        if (language == null || !supportedLanguages.contains(language.getCode())) {
            language = languageService.getLanguage("est");
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
        SOAPMessage message = createSOAPMessage("MobileAuthenticate", childElements);

        SOAPMessage response = sendSOAPMessage(message);

        return parseMobileAuthenticateResponse(response);
    }

    public boolean isAuthenticated(AuthenticationState authenticationState) throws SOAPException {
        String status = AUTHENTICATION_IN_PROGRESS;

        try {
            while (status.equals(AUTHENTICATION_IN_PROGRESS)) {
                status = getAuthenticationStatus(authenticationState);

                if (status == null) {
                    return false;
                }

                if (status.equals(AUTHENTICATION_COMPLETE)) {
                    return true;
                }

                Thread.sleep(getPollingInterval());
            }
        } catch (InterruptedException ignored) {

        }

        return false;
    }

    private String getAuthenticationStatus(AuthenticationState authenticationState) throws SOAPException {
        Map<String, String> childElements = new HashMap<>();
        childElements.put("Sesscode", authenticationState.getSessionCode());
        childElements.put("WaitSignature", "FALSE");
        SOAPMessage message = createSOAPMessage("GetMobileAuthenticateStatus", childElements);

        SOAPMessage response = sendSOAPMessage(message);

        GetMobileAuthenticateStatusResponse getMobileAuthenticateStatusResponse = parseGetMobileAuthenticateStatusResponse(response);

        if (getMobileAuthenticateStatusResponse == null) {
            return null;
        }

        return getMobileAuthenticateStatusResponse.getStatus();
    }

    private SOAPMessage createSOAPMessage(String messageName, Map<String, String> childElements) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();
        envelope.getHeader().detachNode();

        String namespacePrefix = configuration.getString(MOBILEID_NAMESPACE_PREFIX);
        String namespaceURI = configuration.getString(MOBILEID_NAMESPACE_URI);

        Name name = envelope.createName(messageName, namespacePrefix, namespaceURI);
        SOAPBodyElement element = body.addBodyElement(name);

        for (Map.Entry<String, String> child : childElements.entrySet()) {
            Name elementName = envelope.createName(child.getKey());
            SOAPElement soapElement = element.addChildElement(elementName);
            soapElement.addTextNode(child.getValue());
        }

        return message;
    }

    private SOAPMessage sendSOAPMessage(SOAPMessage message) throws SOAPException {
        String endpoint = configuration.getString(MOBILEID_ENDPOINT);
        return connection.call(message, endpoint);
    }

    private Map<String, String> parseSOAPResponse(SOAPMessage message) throws SOAPException {
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody body = envelope.getBody();

        if (body.hasFault()) {
            SOAPFault fault = body.getFault();
            String faultString = fault.getFaultString();
            Detail detail = fault.getDetail();
            String detailMessage = detail.getFirstChild().getTextContent();

            logger.info("SOAPResponse Fault " + faultString + ": " + detailMessage);
            return null;
        }

        SOAPElement responseElement = (SOAPElement) body.getChildElements().next();

        @SuppressWarnings("unchecked")
        Iterator<SOAPElement> iterator = responseElement.getChildElements();
        Map<String, String> response = new HashMap<>();
        while (iterator.hasNext()) {
            SOAPElement element = iterator.next();
            response.put(element.getElementName().getLocalName(), element.getValue());
        }

        return response;
    }

    private MobileAuthenticateResponse parseMobileAuthenticateResponse(SOAPMessage message) throws SOAPException {
        Map<String, String> responseElements = parseSOAPResponse(message);
        if (responseElements == null) {
            return null;
        }

        if (!responseElements.keySet().containsAll(
                Arrays.asList("Sesscode", "UserIDCode", "UserGivenname", "UserSurname", "ChallengeID"))) {
            logger.warn("MobileAuthenticate response is missing one or more required fields.");
            return null;
        }

        if (!responseElements.get("Status").equals("OK")) {
            logger.warn("MobileAuthenticate response is not OK.");
            return null;
        }

        MobileAuthenticateResponse response = new MobileAuthenticateResponse();
        response.setSessionCode(responseElements.get("Sesscode"));
        response.setStatus(responseElements.get("Status"));
        response.setIdCode(responseElements.get("UserIDCode"));
        response.setName(responseElements.get("UserGivenname"));
        response.setSurname(responseElements.get("UserSurname"));
        response.setCountry(responseElements.get("UserCountry"));
        response.setUserCommonName(responseElements.get("UserCN"));
        response.setChallengeID(responseElements.get("ChallengeID"));
        return response;
    }

    private GetMobileAuthenticateStatusResponse parseGetMobileAuthenticateStatusResponse(SOAPMessage message)
            throws SOAPException {
        Map<String, String> elements = parseSOAPResponse(message);

        if (!elements.containsKey("Status")) {
            logger.info("GetMobileAuthenticateStatusResponse response is missing a Status field.");
            return null;
        }

        GetMobileAuthenticateStatusResponse response = new GetMobileAuthenticateStatusResponse();
        response.setStatus(elements.get("Status"));
        return response;
    }

    /**
     * Protected access modifier for testing purposes
     */
    protected int getPollingInterval() {
        return POLLING_INTERVAL_IN_MILLISECONDS;
    }

}
