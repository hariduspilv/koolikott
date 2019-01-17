package ee.hm.dop.service.ehis;

import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.soap.*;

import java.util.*;

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

        Map<String, String> serviceValues = new LinkedHashMap<>();
        serviceValues.put("xRoadInstance", c(XROAD_EHIS_V6_SERVICE_INSTACE));
        serviceValues.put("memberClass", c(XROAD_EHIS_V6_SERVICE_MEMBER_CLASS));
        serviceValues.put("memberCode", c(XROAD_EHIS_V6_SERVICE_MEMBER_CODE));
        serviceValues.put("subsystemCode", c(XROAD_EHIS_V6_SERVICE_SUBSYSTEM_CODE));
        serviceValues.put("serviceCode", c(XROAD_EHIS_V6_SERVICE_SERVICE_NAME));
        serviceValues.put("serviceVersion", c(XROAD_EHIS_V6_SERVICE_SERVICE_VERSION));

        addElements(envelope, iden, serviceElement, serviceValues);

        QName client = envelope.createQName("client", xro);
        SOAPElement clientElement = header.addHeaderElement(client).addAttribute(objectType, "SUBSYSTEM");

        Map<String, String> clientValues = new LinkedHashMap<>();
        clientValues.put("xRoadInstance", c(XROAD_EHIS_V6_SUBSYSTEM_INSTANCE));
        clientValues.put("memberClass", c(XROAD_EHIS_V6_SUBSYSTEM_MEMBER_CLASS));
        clientValues.put("memberCode", c(XROAD_EHIS_V6_SUBSYSTEM_MEMBER_CODE));
        clientValues.put("subsystemCode", c(XROAD_EHIS_V6_SUBSYSTEM_SUBSYSTEM_CODE));

        addElements(envelope, iden, clientElement, clientValues);
    }

    private void addElements(SOAPEnvelope envelope, String iden, SOAPElement serviceElement, Map<String, String> values) throws SOAPException {
        for (Map.Entry<String, String> serviceValue : values.entrySet()) {
            QName elementName = envelope.createQName(serviceValue.getKey(), iden);
            serviceElement.addChildElement(elementName).addTextNode(serviceValue.getValue());
        }
    }

    private void populateBody(String idCode, SOAPEnvelope envelope) throws SOAPException {
        String ehis = c(XROAD_EHIS_V6_NAMESPACE_EHIS_PREFIX);
        QName name = envelope.createQName("isikuRollid", ehis);
        envelope.getBody().addBodyElement(name) //
                .addChildElement("isikukood") //
                .addTextNode(idCode);
    }

    private String c(String name) {
        return configuration.getString(name);
    }
}
