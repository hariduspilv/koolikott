package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.Iterator;

import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import org.easymock.EasyMockRunner;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@RunWith(EasyMockRunner.class)
public class ListIdentifiersConnectorTest {

    @Test
    public void iterator() throws Exception {
        ListIdentifiersConnector builder = getListIdentifiersConnector();

        Element element1 = createMock(Element.class);
        element1.normalize();

        Element element2 = createMock(Element.class);

        NodeList nodeList = createMock(NodeList.class);
        expect(nodeList.item(0)).andReturn(element2);

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element1);
        expect(document.getElementsByTagName("header")).andReturn(nodeList);

        ListIdentifiers firstListIdentifiers = createMock(ListIdentifiers.class);
        expect(firstListIdentifiers.getResumptionToken()).andReturn("resumptionToken");
        expect(firstListIdentifiers.getDocument()).andReturn(document);

        String baseURL = "hostUrl";
        String metadataPrefix = "metadataPrefix";
        DateTime fromDate = new DateTime("2015-01-27T08:14:27");
        expect(builder.newListIdentifier(baseURL, fromDate, metadataPrefix)).andReturn(firstListIdentifiers);

        replay(builder, firstListIdentifiers, element1, document, nodeList, element2);

        Iterator<Element> result = builder.connect(baseURL, fromDate, metadataPrefix).iterator();
        // This is needed to verify that correct parameters were passed to
        // IdentifierIterator constructor
        Element next = result.next();

        verify(builder, firstListIdentifiers, element1, document, nodeList, element2);

        assertTrue(result instanceof IdentifierIterator);
        assertSame(element2, next);
    }

    @Test
    public void connectFailed() throws Exception {
        ListIdentifiersConnector builder = getListIdentifiersConnector();

        String errorMessage = "Failed to connect to repository";
        String baseURL = "hostUrl";
        String metadataPrefix = "metadataPrefix";
        DateTime fromDate = null;
        expect(builder.newListIdentifier(baseURL, fromDate, metadataPrefix)).andThrow(
                new RuntimeException(errorMessage));

        replay(builder);

        try {
            builder.connect(baseURL, fromDate, metadataPrefix);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(builder);
    }

    @Test
    public void connectMetadataPrefixNull() throws Exception {
        ListIdentifiersConnector builder = getListIdentifiersConnector();

        Element element = createMock(Element.class);
        element.normalize();

        NodeList nodeList = createMock(NodeList.class);

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList);

        ListIdentifiers firstListIdentifiers = createMock(ListIdentifiers.class);
        expect(firstListIdentifiers.getResumptionToken()).andReturn("resumptionToken");
        expect(firstListIdentifiers.getDocument()).andReturn(document);

        String baseURL = "hostUrl";
        DateTime fromDate = null;
        expect(builder.newListIdentifier(baseURL, fromDate, null)).andReturn(firstListIdentifiers);

        replay(builder, firstListIdentifiers, element, document, nodeList);

        builder.connect(baseURL, fromDate, null);

        verify(builder, firstListIdentifiers, element, document, nodeList);
    }

    @Test
    public void connectBaseUrlNull() throws Exception {
        ListIdentifiersConnector builder = getListIdentifiersConnector();

        String errorMessage = "Malformed URL";
        String metadataPrefix = "metadataPrefix";
        DateTime fromDate = new DateTime("2015-01-27T08:14:27");
        expect(builder.newListIdentifier(null, fromDate, metadataPrefix)).andThrow(new RuntimeException(errorMessage));

        replay(builder);

        try {
            builder.connect(null, fromDate, metadataPrefix);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(builder);
    }

    private ListIdentifiersConnector getListIdentifiersConnector() throws NoSuchMethodException {
        Method newListIdentifier = ListIdentifiersConnector.class.getDeclaredMethod("newListIdentifier", String.class,
                DateTime.class, String.class);
        return createMockBuilder(ListIdentifiersConnector.class).addMockedMethod(newListIdentifier).createMock();
    }
}
