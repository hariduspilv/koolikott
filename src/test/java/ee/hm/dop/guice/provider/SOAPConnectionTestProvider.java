package ee.hm.dop.guice.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Guice provider of SOAPConnection.
 */
@Singleton
public class SOAPConnectionTestProvider implements Provider<SOAPConnection> {

    @Override
    public synchronized SOAPConnection get() {
        return new SOAPConnectionMock();
    }
}

class SOAPConnectionMock extends SOAPConnection {

    private static final Map<String, Map<String, String>> mobileAuthenticateResponses;
    private static final Map<String, Map<String, String>> getMobileAuthenticateStatusResponses;

    static {
        // The key is phone number
        mobileAuthenticateResponses = new HashMap<>();
        Map<String, String> response;

        response = new HashMap<>();
        response.put("Sesscode", "8927225289");
        response.put("Status", "OK");
        response.put("UserIDCode", "22334455667");
        response.put("UserGivenname", "Matt");
        response.put("UserSurname", "Smith");
        response.put("UserCountry", "EE");
        response.put("UserCN", "MATT,SMITH,22334455667");
        response.put("ChallengeID", "1111");
        mobileAuthenticateResponses.put("55551234", response);

        response = new HashMap<>();
        response.put("Sesscode", "782652658");
        response.put("Status", "OK");
        response.put("UserIDCode", "33445566778");
        response.put("UserGivenname", "Matt");
        response.put("UserSurname", "Smith");
        response.put("UserCountry", "EE");
        response.put("UserCN", "MATT,SMITH,33445566778");
        response.put("ChallengeID", "1111");
        mobileAuthenticateResponses.put("44441234", response);

        // The key is session code (Sesscode)
        getMobileAuthenticateStatusResponses = new HashMap<>();

        response = new HashMap<>();
        response.put("Status", "USER_AUTHENTICATED");
        getMobileAuthenticateStatusResponses.put("8927225289", response);

        response = new HashMap<>();
        response.put("Status", "NOT_VALID");
        getMobileAuthenticateStatusResponses.put("782652658", response);
    }

    @Override
    public SOAPMessage call(SOAPMessage request, Object to) throws SOAPException {
        SOAPBody body = request.getSOAPPart().getEnvelope().getBody();
        SOAPElement requestElement = (SOAPElement) body.getChildElements().next();
        String elementName = requestElement.getElementName().getLocalName();

        switch (elementName) {
            case "MobileAuthenticate":
                return mobileAuthenticate(parseRequest(request));
            case "GetMobileAuthenticateStatus":
                return getMobileAuthenticateStatus(parseRequest(request));
        }

        return null;
    }

    private SOAPMessage mobileAuthenticate(Map<String, String> request) throws SOAPException {
        String phoneNumber = request.get("PhoneNo");
        if (mobileAuthenticateResponses.containsKey(phoneNumber)) {
            return createSOAPMessage("MobileAuthenticateResponse", mobileAuthenticateResponses.get(phoneNumber));
        }
        return null;
    }

    private SOAPMessage getMobileAuthenticateStatus(Map<String, String> request) throws SOAPException {
        String sessionCode = request.get("Sesscode");
        if (getMobileAuthenticateStatusResponses.containsKey(sessionCode)) {
            return createSOAPMessage("GetMobileAuthenticateStatusResponse",
                    getMobileAuthenticateStatusResponses.get(sessionCode));
        }
        return null;
    }

    private Map<String, String> parseRequest(SOAPMessage request) throws SOAPException {
        SOAPBody body = request.getSOAPPart().getEnvelope().getBody();
        SOAPElement responseElement = (SOAPElement) body.getChildElements().next();

        @SuppressWarnings("unchecked")
        Iterator<SOAPElement> iterator = responseElement.getChildElements();
        Map<String, String> message = new HashMap<>();
        while (iterator.hasNext()) {
            SOAPElement element = iterator.next();
            message.put(element.getElementName().getLocalName(), element.getValue());
        }
        return message;
    }

    public SOAPMessage createSOAPMessage(String messageName, Map<String, String> childElements) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();
        envelope.getHeader().detachNode();

        String namespacePrefix = "pre";
        String namespaceURI = "http://www.example.com/Service/Service.wsdl";

        Name name = envelope.createName(messageName, namespacePrefix, namespaceURI);
        SOAPBodyElement element = body.addBodyElement(name);

        for (Map.Entry<String, String> child : childElements.entrySet()) {
            Name elementName = envelope.createName(child.getKey());
            SOAPElement soapElement = element.addChildElement(elementName);
            soapElement.addTextNode(child.getValue());
        }

        return message;
    }

    @Override
    public void close() throws SOAPException {
    }

}