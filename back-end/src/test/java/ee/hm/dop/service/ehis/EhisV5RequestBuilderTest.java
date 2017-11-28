package ee.hm.dop.service.ehis;

import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import ee.hm.dop.common.test.GuiceTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceTestRunner.class)
public class EhisV5RequestBuilderTest {

    @Inject
    private EhisV5RequestBuilder ehisV5RequestBuilder;

    @Test
    public void v5_generates_xml() throws Exception {
        SOAPMessage message = ehisV5RequestBuilder.createGetPersonInformationSOAPMessage("123");
        assertTrue(message != null);
//        File file = new File("./src/test/resources/ehis/generated_soap_v5.xml");
//        try (OutputStream outputStream = new FileOutputStream(file)) {
//            message.writeTo(outputStream);
//            outputStream.flush();
//        }

        SOAPHeader header = message.getSOAPHeader();
        Iterator<Node> headerElements = (Iterator<Node>) header.getChildElements();
        while (headerElements.hasNext()) {
            validateHeader(headerElements.next());
        }
        SOAPBody body = message.getSOAPBody();
        Iterator<Node> bodyElements = (Iterator<Node>) body.getChildElements();
        while (bodyElements.hasNext()) {
            Node firstChild = bodyElements.next();
            validateBodyFirstLevel(firstChild);
            org.w3c.dom.Node secondChild = firstChild.getFirstChild();
            validateBodySecondLevel(secondChild);
            org.w3c.dom.Node thirdChild = secondChild.getFirstChild();
            validateBodyThirdLevel(thirdChild);
        }
    }

    private void validateHeader(Node element) {
        if (logicBlock(element, "andmekogu", "ehis")) return;
        if (logicBlock(element, "isikukood", "123")) return;
        if (logicBlock(element, "nimi", "ehis.isiku_rollid.v1")) return;
        if (logicBlock(element, "id", "3ff13834-248b-4d0f-9ff0-585b729e7b27")) return;
        if (logicBlock(element, "asutus", "10585438")) return;
        throw new UnsupportedOperationException("unknown element: " + element.getLocalName());
    }

    private void validateBodyFirstLevel(org.w3c.dom.Node element) {
        if (bodyLogicBlock(element, "isiku_rollid", null)) return;
        throw new UnsupportedOperationException("unknown element: " + element.getLocalName());
    }

    private void validateBodySecondLevel(org.w3c.dom.Node secondChild) {
        if (bodyLogicBlock(secondChild, "keha", null)) return;
        throw new UnsupportedOperationException("unknown element: " + secondChild.getLocalName());
    }

    private void validateBodyThirdLevel(org.w3c.dom.Node secondChild) {
        if (bodyLogicBlock(secondChild, "isikukood", "123")) return;
        throw new UnsupportedOperationException("unknown element: " + secondChild.getLocalName());
    }

    private boolean logicBlock(Node element, String localName, String value) {
        if (element.getLocalName().equals(localName)) {
            if (!element.getLocalName().equals("id")) {
                assertEquals(value, element.getValue());
            } else {
                assertEquals(value.length(), element.getValue().length());
            }
            assertEquals("xtee:" + localName, element.getNodeName());
            return true;
        }
        return false;
    }

    private boolean bodyLogicBlock(org.w3c.dom.Node element, String localName, String value) {
        ElementImpl element2 = (ElementImpl) element;
        if (element2.getLocalName().equals(localName)) {
            if (!element2.getLocalName().equals("id")) {
                assertEquals(value, element2.getValue());
            } else {
                assertEquals(value.length(), element2.getValue().length());
            }
            assertEquals(localName, element2.getNodeName());
            return true;
        }
        return false;
    }
}