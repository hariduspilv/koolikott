package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.EHIS_ENDPOINT;
import static ee.hm.dop.utils.ConfigurationProperties.EHIS_INSTITUTION;
import static ee.hm.dop.utils.ConfigurationProperties.EHIS_REQUESTER_ID_CODE;
import static ee.hm.dop.utils.ConfigurationProperties.EHIS_SERVICE_NAME;
import static ee.hm.dop.utils.ConfigurationProperties.EHIS_SYSTEM_NAME;
import static ee.hm.dop.utils.ConfigurationProperties.XTEE_NAMESPACE_PREFIX;
import static ee.hm.dop.utils.ConfigurationProperties.XTEE_NAMESPACE_URI;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import ee.hm.dop.ehis.EhisParser;
import ee.hm.dop.model.ehis.Person;

public class EhisSOAPService {

    private static Logger logger = LoggerFactory.getLogger(EhisSOAPService.class);

    @Inject
    private EhisParser ehisParser;

    @Inject
    private Configuration configuration;

    @Inject
    private SOAPConnection connection;

    public Person getPersonInformation(String idCode) {
        Person person = null;
        try {
            SOAPMessage getPersonInformationMessage = createGetPersonInformationSOAPMessage(idCode);
            SOAPMessage response = sendSOAPMessage(getPersonInformationMessage);
            String xmlResponse = parseSOAPResponse(response);
            person = ehisParser.parse(xmlResponse);
        } catch (Exception e) {
            logger.error("Error getting User information from EHIS.", e);
        }

        return person;
    }

    private SOAPMessage createGetPersonInformationSOAPMessage(String idCode) throws SOAPException {
        SOAPMessage message = MessageFactory.newInstance().createMessage();

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        populateHeader(envelope);
        populateBody(idCode, envelope);

        return message;
    }

    private void populateHeader(SOAPEnvelope envelope) throws SOAPException {
        String namespacePrefix = configuration.getString(XTEE_NAMESPACE_PREFIX);
        String namespaceURI = configuration.getString(XTEE_NAMESPACE_URI);

        Map<String, String> headerValues = new HashMap<>();
        headerValues.put("asutus", configuration.getString(EHIS_INSTITUTION));
        headerValues.put("andmekogu", configuration.getString(EHIS_SYSTEM_NAME));
        headerValues.put("isikukood", configuration.getString(EHIS_REQUESTER_ID_CODE));
        headerValues.put("id", UUID.randomUUID().toString());
        headerValues.put("nimi", configuration.getString(EHIS_SERVICE_NAME));

        SOAPHeader header = envelope.getHeader();
        for (Map.Entry<String, String> headerValue : headerValues.entrySet()) {
            Name elementName = envelope.createName(headerValue.getKey(), namespacePrefix, namespaceURI);
            header.addHeaderElement(elementName).addTextNode(headerValue.getValue());
        }
    }

    private void populateBody(String idCode, SOAPEnvelope envelope) throws SOAPException {
        Name name = envelope.createName("isiku_rollid");
        envelope.getBody().addBodyElement(name) //
                .addChildElement("keha") //
                .addChildElement("isikukood") //
                .addTextNode(idCode);
    }

    private SOAPMessage sendSOAPMessage(SOAPMessage message) throws SOAPException {
        String endpoint = configuration.getString(EHIS_ENDPOINT);
        return connection.call(message, endpoint);
    }

    private String parseSOAPResponse(SOAPMessage message) throws Exception {
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

        Node person = body.getElementsByTagName("isik").item(0);
        DOMSource source = new DOMSource(person);
        StringWriter stringResult = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
        return stringResult.toString();
    }
}
