package ee.hm.dop.config.soap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

@Component
@Profile("it")
public class EhisSOAPConnection implements SOAPConnectionMockI {

    private static final String RESPONSE_FILE_NAME = "/ehis/response_%s.xml";

    @Override
    public SOAPMessage call(SOAPMessage request, Object to) throws SOAPException {
        SOAPBody body = request.getSOAPPart().getEnvelope().getBody();
        String idCode = body.getElementsByTagName("isikukood").item(0).getTextContent();

        return getSoapMessageFromFile(String.format(RESPONSE_FILE_NAME, idCode));
    }

    private SOAPMessage getSoapMessageFromFile(String fileName) {
        try (InputStream soapMessageIS = EhisSOAPConnection.class.getResourceAsStream(fileName)) {
            return MessageFactory.newInstance().createMessage(new MimeHeaders(), soapMessageIS);
        } catch (Exception e) {
            return null;
        }
    }
}
