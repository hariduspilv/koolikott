package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_ENDPOINT;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_NAMESPACE_PREFIX;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_NAMESPACE_URI;

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

import org.apache.commons.configuration.Configuration;

public class MobileIDSOAPService {

    @Inject
    private Configuration configuration;

    @Inject
    private SOAPConnection connection;

    public SOAPMessage createSOAPMessage(String messageName, Map<String, String> childElements) throws SOAPException {
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

    public SOAPMessage sendSOAPMessage(SOAPMessage message) throws SOAPException {
        String endpoint = configuration.getString(MOBILEID_ENDPOINT);
        return connection.call(message, endpoint);
    }

    public Map<String, String> parseSOAPResponse(SOAPMessage message) throws SOAPException {
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody body = envelope.getBody();

        if (body.hasFault()) {
            SOAPFault fault = body.getFault();
            String faultString = fault.getFaultString();
            Detail detail = fault.getDetail();
            String detailMessage = detail.getFirstChild().getTextContent();

            throw new RuntimeException("SOAPResponse Fault " + faultString + ": " + detailMessage);
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

}
