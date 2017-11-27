package ee.hm.dop.service.ehis;

import org.apache.commons.configuration.Configuration;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.soap.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static ee.hm.dop.utils.ConfigurationProperties.*;

public class EhisV6RequestBuilder {

    @Inject
    private Configuration configuration;

    public SOAPMessage createGetPersonInformationSOAPMessage(String idCode) throws SOAPException {
        SOAPMessage message = MessageFactory.newInstance().createMessage();

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration(c(XROAD_EHIS_V6_NAMESPACE_XRO_PREFIX), c(XROAD_EHIS_V6_NAMESPACE_XRO_URI));
        envelope.addNamespaceDeclaration(c(XROAD_EHIS_V6_NAMESPACE_IDEN_PREFIX), c(XROAD_EHIS_V6_NAMESPACE_IDEN_URI));
        envelope.addNamespaceDeclaration(c(XROAD_EHIS_V6_NAMESPACE_EHIS_PREFIX), c(XROAD_EHIS_V6_NAMESPACE_EHIS_URI));

        populateHeader(envelope, idCode);
        populateBody(idCode, envelope);

        return message;
    }

    private void populateHeader(SOAPEnvelope envelope, String idCode) throws SOAPException {
        String xro = c(XROAD_EHIS_V6_NAMESPACE_XRO_PREFIX);
        String iden = c(XROAD_EHIS_V6_NAMESPACE_IDEN_PREFIX);

        Map<String, String> headerValues = new HashMap<>();
        headerValues.put("protocolVersion", c(XROAD_EHIS_V6_HEADER_PROTOCOL));
        headerValues.put("issue", c(XROAD_EHIS_V6_HEADER_ISSUE));
        headerValues.put("userId", idCode);
        headerValues.put("id", UUID.randomUUID().toString());

        SOAPHeader header = envelope.getHeader();
        for (Map.Entry<String, String> headerValue : headerValues.entrySet()) {
            QName elementName = envelope.createQName(headerValue.getKey(), xro);
            header.addHeaderElement(elementName).addTextNode(headerValue.getValue());
        }

        QName service = envelope.createQName("service", xro);
        QName objectType = envelope.createQName("objectType", iden);
        SOAPElement serviceElement = header.addHeaderElement(service).addAttribute(objectType, "SERVICE");

        Map<String, String> serviceValues = new HashMap<>();
        serviceValues.put("xRoadInstance", c(XROAD_EHIS_V6_SERVICE_INSTACE));
        serviceValues.put("memberClass", c(XROAD_EHIS_V6_SERVICE_MEMBER_CLASS));
        serviceValues.put("memberCode", c(XROAD_EHIS_V6_SERVICE_MEMBER_CODE));
        serviceValues.put("subsystemCode", c(XROAD_EHIS_V6_SERVICE_SUBSYSTEM_CODE));
        serviceValues.put("serviceCode", c(XROAD_EHIS_V6_SERVICE_SERVICE_NAME));
        serviceValues.put("serviceVersion", c(XROAD_EHIS_V6_SERVICE_SERVICE_VERSION));

        for (Map.Entry<String, String> serviceValue : serviceValues.entrySet()) {
            QName elementName = envelope.createQName(serviceValue.getKey(), iden);
            serviceElement.addChildElement(elementName).addTextNode(serviceValue.getValue());
        }

        QName client = envelope.createQName("client", xro);
        SOAPElement clientElement = header.addHeaderElement(client).addAttribute(objectType, "SUBSYSTEM");

        Map<String, String> clientValues = new HashMap<>();
        clientValues.put("xRoadInstance", c(XROAD_EHIS_V6_SUBSYSTEM_INSTANCE));
        clientValues.put("memberClass", c(XROAD_EHIS_V6_SUBSYSTEM_MEMBER_CLASS));
        clientValues.put("memberCode", c(XROAD_EHIS_V6_SUBSYSTEM_MEMBER_CODE));
        clientValues.put("subsystemCode", c(XROAD_EHIS_V6_SUBSYSTEM_SUBSYSTEM_CODE));

        for (Map.Entry<String, String> clientValue : clientValues.entrySet()) {
            QName elementName = envelope.createQName(clientValue.getKey(), iden);
            clientElement.addChildElement(elementName).addTextNode(clientValue.getValue());
        }
    }

    private void populateBody(String idCode, SOAPEnvelope envelope) throws SOAPException {
        /*Name name = envelope.createName("isiku_rollid");
        envelope.getBody().addBodyElement(name) //
                .addChildElement("keha") //
                .addChildElement("isikukood") //
                .addTextNode(idCode);*/
    }

    private String c(String name) {
        return configuration.getString(name);
    }
}
