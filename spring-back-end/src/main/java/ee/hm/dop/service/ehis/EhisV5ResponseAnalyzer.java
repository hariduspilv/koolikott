package ee.hm.dop.service.ehis;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@Transactional
public class EhisV5ResponseAnalyzer {

    public String parseSOAPResponse(SOAPMessage message) throws Exception {
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
