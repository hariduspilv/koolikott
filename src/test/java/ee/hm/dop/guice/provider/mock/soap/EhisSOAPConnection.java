package ee.hm.dop.guice.provider.mock.soap;

import java.io.InputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class EhisSOAPConnection implements SOAPConnectionMockI {

    private static final String RESPONSE_FILE_NAME = "/ehis/response_%s.xml";

    @Override
    public SOAPMessage call(SOAPMessage request, Object to) throws SOAPException {
        SOAPBody body = request.getSOAPPart().getEnvelope().getBody();
        String idCode = body.getElementsByTagName("isikukood").item(0).getTextContent();

        return getSoapMessageFromFile(String.format(RESPONSE_FILE_NAME, idCode));
    }

    private SOAPMessage getSoapMessageFromFile(String fileName) {
        SOAPMessage message = null;
        try (InputStream soapMessageIS = EhisSOAPConnection.class.getResourceAsStream(fileName)) {
            MessageFactory factory = MessageFactory.newInstance();
            message = factory.createMessage(new MimeHeaders(), soapMessageIS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return message;
    }
}
