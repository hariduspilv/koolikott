package ee.hm.dop.service.ehis;

import org.w3c.dom.Node;

import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class EhisV6ResponseAnalyzer {

    public String parseSOAPResponse(SOAPMessage message) throws Exception {
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody body = envelope.getBody();

        Node person = body.getElementsByTagName("isik").item(0);
        if (person != null) {
            DOMSource source = new DOMSource(person);
            StringWriter stringResult = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
            return stringResult.toString();
        }

        Node errorCode = body.getElementsByTagName("veakood").item(0);
        if (errorCode != null) {
            throw new RuntimeException("Error retrieving information from EHIS: " + errorCode.getNodeName() + ": " + errorCode.getTextContent());
        }
        throw new RuntimeException("Unknown error from ehis");
    }
}
