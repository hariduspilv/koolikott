package ee.hm.dop.service.ehis;

import org.apache.commons.configuration.Configuration;

import javax.inject.Inject;
import javax.xml.soap.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static ee.hm.dop.utils.ConfigurationProperties.EHIS_SERVICE_NAME;

public class EhisV6RequestBuilder {

    @Inject
    private Configuration configuration;

    public SOAPMessage createGetPersonInformationSOAPMessage(String idCode) throws SOAPException {
        SOAPMessage message = MessageFactory.newInstance().createMessage();

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        populateHeader(envelope, idCode);
        populateBody(idCode, envelope);

        return message;
    }

    private void populateHeader(SOAPEnvelope envelope, String idCode) throws SOAPException {
        /*String namespacePrefix = configuration.getString(XTEE_NAMESPACE_PREFIX);
        String namespaceURI = configuration.getString(XTEE_NAMESPACE_URI);

        Map<String, String> headerValues = new HashMap<>();
        headerValues.put("asutus", configuration.getString(EHIS_INSTITUTION));
        headerValues.put("andmekogu", configuration.getString(EHIS_SYSTEM_NAME));
        headerValues.put("isikukood", idCode);
        headerValues.put("id", UUID.randomUUID().toString());
        headerValues.put("nimi", configuration.getString(EHIS_SERVICE_NAME));

        SOAPHeader header = envelope.getHeader();
        for (Map.Entry<String, String> headerValue : headerValues.entrySet()) {
            Name elementName = envelope.createName(headerValue.getKey(), namespacePrefix, namespaceURI);
            header.addHeaderElement(elementName).addTextNode(headerValue.getValue());
        }*/
    }

    private void populateBody(String idCode, SOAPEnvelope envelope) throws SOAPException {
        /*Name name = envelope.createName("isiku_rollid");
        envelope.getBody().addBodyElement(name) //
                .addChildElement("keha") //
                .addChildElement("isikukood") //
                .addTextNode(idCode);*/
    }
}
