package ee.hm.dop.service.ehis;

import ee.hm.dop.common.test.DatabaseTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EhisV6RequestBuilderTest extends DatabaseTestBase {

    public static final String XRO = "xro:";
    @Inject
    private EhisV6RequestBuilder ehisV6RequestBuilder;

    @Test
    public void v6_generates_xml() throws Exception {
        SOAPMessage message = ehisV6RequestBuilder.createGetPersonInformationSOAPMessage("123");
        assertTrue(message != null);
//        File file = new File("./back-end/src/test/resources/ehis/generated_soap_v6.xml");
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
            Node secondChild = (Node) firstChild.getFirstChild();
            validateBodySecondLevel(secondChild);
        }
    }

    private void validateHeader(Node element) {
        if (logicBlock(element, XRO, "issue", "ehis")) return;
        else if (logicBlock(element, XRO, "userId", "123")) return;
        else if (logicBlock(element, XRO, "protocolVersion", "4.0")) return;
        else if (logicBlock(element, XRO, "id", "3ff13834-248b-4d0f-9ff0-585b729e7b27")) return;
        else if (element.getLocalName().equals("service")) {
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = (Node) childNodes.item(i);
                if (logicBlock(node, "iden:", "memberCode", "70000740")) continue;
                if (logicBlock(node, "iden:", "serviceVersion", "v1")) continue;
                if (logicBlock(node, "iden:", "serviceCode", "isiku_rollid")) continue;
                if (logicBlock(node, "iden:", "xRoadInstance", "ee")) continue;
                if (logicBlock(node, "iden:", "memberClass", "gov")) continue;
                if (logicBlock(node, "iden:", "subsystemCode", "ehis")) continue;
                else throw new UnsupportedOperationException("unknown element: " + node.getLocalName());
            }
        } else if (element.getLocalName().equals("client")) {
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = (Node) childNodes.item(i);
                if (logicBlock(node, "iden:", "memberCode", "10585438")) continue;
                if (logicBlock(node, "iden:", "xRoadInstance", "ee")) continue;
                if (logicBlock(node, "iden:", "memberClass", "gov")) continue;
                if (logicBlock(node, "iden:", "subsystemCode", "10585438")) continue;
                else throw new UnsupportedOperationException("unknown element: " + node.getLocalName());
            }
        } else throw new UnsupportedOperationException("unknown element: " + element.getLocalName());
    }

    private void validateBodyFirstLevel(Node element) {
        if (logicBlock(element, "ehis:", "isikuRollid", null)) return;
        throw new UnsupportedOperationException("unknown element: " + element.getLocalName());
    }

    private void validateBodySecondLevel(Node secondChild) {
        if (logicBlock(secondChild, "", "isikukood", "123")) return;
        throw new UnsupportedOperationException("unknown element: " + secondChild.getLocalName());
    }

    private boolean logicBlock(Node element, String prefix, String localName, String value) {
        if (element.getLocalName().equals(localName)) {
            if (!element.getLocalName().equals("id")) {
                assertEquals(value, element.getValue());
            } else {
                assertEquals(value.length(), element.getValue().length());
            }
            assertEquals(prefix + localName, element.getNodeName());
            return true;
        }
        return false;
    }
}