package ee.hm.dop.service.ehis;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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

import ee.hm.dop.model.ehis.Person;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class EhisSOAPService implements IEhisSOAPService {

    private static Logger logger = LoggerFactory.getLogger(EhisSOAPService.class);
    @Inject
    private EhisParser ehisParser;
    @Inject
    private Configuration configuration;
    @Inject
    private SOAPConnection connection;
    @Inject
    private EhisV5RequestBuilder ehisV5RequestBuilder;
    @Inject
    private EhisV6RequestBuilder ehisV6RequestBuilder;

    @Override
    public Person getPersonInformation(String idCode) {
        try {
            boolean useV6 = configuration.getBoolean(XROAD_EHIS_USE_V6);
            SOAPMessage message;
            if (useV6) {
                message = ehisV6RequestBuilder.createGetPersonInformationSOAPMessage(idCode);
            } else {
                message = ehisV5RequestBuilder.createGetPersonInformationSOAPMessage(idCode);
            }


            if (logger.isInfoEnabled()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                message.writeTo(out);
                String strMsg = new String(out.toByteArray(), StandardCharsets.UTF_8);
                logger.info(format("Sending message to EHIS: %s", strMsg));
            }

            SOAPMessage response = sendSOAPMessage(message);
            String xmlResponse = parseSOAPResponse(response);

            logger.info(format("Received response from EHIS: %s", xmlResponse));

            return ehisParser.parse(xmlResponse);
        } catch (Exception e) {
            logger.error("Error getting User information from EHIS.", e);
            return null;
        }

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

            throw new RuntimeException("Error retrieving information from EHIS: " + faultString + ": " + detailMessage);
        }

        Node person = body.getElementsByTagName("isik").item(0);
        DOMSource source = new DOMSource(person);
        StringWriter stringResult = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
        return stringResult.toString();
    }
}
