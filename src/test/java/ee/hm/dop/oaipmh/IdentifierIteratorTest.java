package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import ee.hm.dop.oaipmh.IdentifierIterator.IdentifierIteratorBuilder;

@RunWith(EasyMockRunner.class)
public class IdentifierIteratorTest {

    @Mock
    private ListIdentifiers firstListIdentifiers;

    @Mock
    private ListIdentifiers withTokenListIdentifiers;

    @Test
    public void build() throws Exception {
        IdentifierIteratorBuilder builder = getIdentifierIteratorBuilder();

        Element element = createMock(Element.class);
        element.normalize();

        Node node = createMock(Node.class);

        NodeList nodeList = createMock(NodeList.class);
        expect(nodeList.item(0)).andReturn(node);

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList);

        expect(firstListIdentifiers.getResumptionToken()).andReturn("resumptionToken");
        expect(firstListIdentifiers.getDocument()).andReturn(document);

        String baseURL = "hostUrl";
        String metadataPrefix = "metadataPrefix";
        expect(builder.newListIdentifier(baseURL, metadataPrefix)).andReturn(firstListIdentifiers);

        replay(builder, firstListIdentifiers, element, document, nodeList, node);

        Iterator<Node> result = builder.build(baseURL, metadataPrefix);
        // This is needed to verify that correct parameters were passed to IdentifierIterator constructor
        Node next = result.next();

        verify(builder, firstListIdentifiers, element, document, nodeList, node);

        assertTrue(result instanceof IdentifierIterator);
        assertSame(node, next);
    }

    @Test
    public void buildFailedConnectingToRepository() throws Exception {
        IdentifierIteratorBuilder builder = getIdentifierIteratorBuilder();

        String errorMessage = "Failed to connect to repository";
        String baseURL = "hostUrl";
        String metadataPrefix = "metadataPrefix";
        expect(builder.newListIdentifier(baseURL, metadataPrefix)).andThrow(new RuntimeException(errorMessage));

        replay(builder);

        try {
            builder.build(baseURL, metadataPrefix);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(builder);
    }

    @Test
    public void buildMetadataPrefixNull() throws Exception {
        IdentifierIteratorBuilder builder = getIdentifierIteratorBuilder();

        Element element = createMock(Element.class);
        element.normalize();

        Node node = createMock(Node.class);

        NodeList nodeList = createMock(NodeList.class);
        expect(nodeList.item(0)).andReturn(node);

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList);

        expect(firstListIdentifiers.getResumptionToken()).andReturn("resumptionToken");
        expect(firstListIdentifiers.getDocument()).andReturn(document);

        String baseURL = "hostUrl";
        String metadataPrefix = null;
        expect(builder.newListIdentifier(baseURL, metadataPrefix)).andReturn(firstListIdentifiers);

        replay(builder, firstListIdentifiers, element, document, nodeList, node);

        Iterator<Node> result = builder.build(baseURL, metadataPrefix);
        // This is needed to verify that correct parameters were passed to IdentifierIterator constructor
        Node next = result.next();

        verify(builder, firstListIdentifiers, element, document, nodeList, node);

        assertTrue(result instanceof IdentifierIterator);
        assertSame(node, next);
    }

    @Test
    public void buildBaseUrlNull() throws Exception {
        IdentifierIteratorBuilder builder = getIdentifierIteratorBuilder();

        String errorMessage = "Malformed URL";
        String baseURL = null;
        String metadataPrefix = "metadataPrefix";
        expect(builder.newListIdentifier(baseURL, metadataPrefix)).andThrow(new RuntimeException(errorMessage));

        replay(builder);

        try {
            builder.build(baseURL, metadataPrefix);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(builder);
    }

    private IdentifierIteratorBuilder getIdentifierIteratorBuilder() throws NoSuchMethodException {
        Method newListIdentifier = IdentifierIteratorBuilder.class
                .getDeclaredMethod("newListIdentifier", String.class, String.class);
        return createMockBuilder(IdentifierIteratorBuilder.class).addMockedMethod(newListIdentifier).createMock();
    }

    private IdentifierIterator getIdentifierIterator(NodeList headers, String baseURL, String resumptionToken)
            throws NoSuchMethodException {
        Constructor constructor = IdentifierIterator.class
                .getDeclaredConstructor(NodeList.class, String.class, String.class);

        Method newListIdentifier = IdentifierIterator.class
                .getDeclaredMethod("newListIdentifier", String.class, String.class);

        return createMockBuilder(IdentifierIterator.class).addMockedMethod(newListIdentifier)
                .withConstructor(constructor).withArgs(headers, baseURL, resumptionToken).createMock();
    }

    @Test
    public void next() throws Exception {
        Node node = createMock(Node.class);

        NodeList nodeList = createMock(NodeList.class);
        expect(nodeList.item(0)).andReturn(node);

        replay(node, nodeList);

        Node next = getIdentifierIterator(nodeList, null, null).next();

        verify(node, nodeList);
        assertSame(node, next);
    }

    @Test
    public void nextWhenNoNext() throws Exception {
        NodeList nodeList = createMock(NodeList.class);
        String errorMessage = "No element";
        expect(nodeList.item(0)).andThrow(new RuntimeException(errorMessage));

        replay(nodeList);

        try {
            getIdentifierIterator(nodeList, null, null).next();
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertTrue(e instanceof NoSuchElementException);
        }

        verify(nodeList);
    }

    @Test
    public void hasNext() throws Exception {
        NodeList nodeList = createMock(NodeList.class);
        expect(nodeList.getLength()).andReturn(1);

        replay(nodeList);

        boolean result = getIdentifierIterator(nodeList, null, null).hasNext();

        verify(nodeList);

        assertTrue(result);
    }

    @Test
    public void hasNextHeadersIsNull() throws Exception {
        boolean result = getIdentifierIterator(null, null, null).hasNext();
        assertFalse(result);
    }

    @Test
    public void hasNextFirstHeadersAllConsumedThenGetNewHeaders() throws Exception {
        String baseURL = "repositoryURL";
        String resumptionToken = "token";

        NodeList nodeList1 = createMock(NodeList.class);
        expect(nodeList1.getLength()).andReturn(0); // same as index == headers.length()

        Node node = createMock(Node.class);

        NodeList nodeList2 = createMock(NodeList.class);
        expect(nodeList2.getLength()).andReturn(1);
        expect(nodeList2.item(0)).andReturn(node);

        Element element = createMock(Element.class);
        element.normalize();

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList2);

        expect(withTokenListIdentifiers.getDocument()).andReturn(document);

        IdentifierIterator identifierIterator = getIdentifierIterator(nodeList1, baseURL, resumptionToken);
        expect(identifierIterator.newListIdentifier(baseURL, resumptionToken)).andReturn(withTokenListIdentifiers);

        replay(nodeList1, nodeList2, node, element, document, withTokenListIdentifiers, identifierIterator);

        boolean result = identifierIterator.hasNext();
        // This is needed to verify that correct parameters were passed to IdentifierIterator constructor
        Node next = identifierIterator.next();

        verify(nodeList1, nodeList2, node, element, document, withTokenListIdentifiers, identifierIterator);

        assertTrue(result);
        assertSame(node, next);
    }

    @Test
    public void hasNextFirstHeadersAllConsumedThenGetNewHeadersNull() throws Exception {
        String baseURL = "repositoryURL";
        String resumptionToken = "token";

        NodeList nodeList1 = createMock(NodeList.class);
        expect(nodeList1.getLength()).andReturn(0).times(2); // same as index == headers.length()

        IdentifierIterator identifierIterator = getIdentifierIterator(nodeList1, baseURL, resumptionToken);
        expect(identifierIterator.newListIdentifier(baseURL, resumptionToken)).andReturn(null);

        replay(nodeList1, identifierIterator);

        boolean result = identifierIterator.hasNext();

        verify(nodeList1, identifierIterator);

        assertFalse(result);
    }

    @Test
    public void hasNextFirstHeadersAllConsumedAndNewHeadersEmpty() throws Exception {
        String baseURL = "repositoryURL";
        String resumptionToken = "token";

        NodeList nodeList1 = createMock(NodeList.class);
        expect(nodeList1.getLength()).andReturn(0); // same as index == headers.length()

        NodeList nodeList2 = createMock(NodeList.class);
        expect(nodeList2.getLength()).andReturn(0);

        Element element = createMock(Element.class);
        element.normalize();

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList2);

        expect(withTokenListIdentifiers.getDocument()).andReturn(document);

        IdentifierIterator identifierIterator = getIdentifierIterator(nodeList1, baseURL, resumptionToken);
        expect(identifierIterator.newListIdentifier(baseURL, resumptionToken)).andReturn(withTokenListIdentifiers);

        replay(nodeList1, nodeList2, element, document, withTokenListIdentifiers, identifierIterator);

        boolean result = identifierIterator.hasNext();

        verify(nodeList1, nodeList2, element, document, withTokenListIdentifiers, identifierIterator);

        assertFalse(result);
    }

    /**
     * This tests the flow while hasNext, get next. It does not care about returning the correct nodes, it is tested in
     * the other tests.
     *
     * @throws Exception
     */
    @Test
    public void consumeAll() throws Exception {
        String baseURL = "repositoryURL";
        String resumptionToken = "token";

        Node node = createMock(Node.class);

        NodeList nodeList1 = createMock(NodeList.class);
        expect(nodeList1.getLength()).andReturn(3).times(4);
        expect(nodeList1.item(0)).andReturn(node);
        expect(nodeList1.item(1)).andReturn(node);
        expect(nodeList1.item(2)).andReturn(node);

        NodeList nodeList2 = createMock(NodeList.class);
        expect(nodeList2.getLength()).andReturn(2).times(4);
        expect(nodeList2.item(0)).andReturn(node);
        expect(nodeList2.item(1)).andReturn(node);

        Element element = createMock(Element.class);
        element.normalize();

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList2);

        expect(withTokenListIdentifiers.getDocument()).andReturn(document);

        IdentifierIterator identifierIterator = getIdentifierIterator(nodeList1, baseURL, resumptionToken);
        expect(identifierIterator.newListIdentifier(baseURL, resumptionToken)).andReturn(withTokenListIdentifiers);
        expect(identifierIterator.newListIdentifier(baseURL, resumptionToken)).andReturn(null);

        replay(nodeList1, nodeList2, node, element, document, withTokenListIdentifiers, identifierIterator);

        while (identifierIterator.hasNext()) {
            identifierIterator.next();
        }

        verify(nodeList1, nodeList2, node, element, document, withTokenListIdentifiers, identifierIterator);
    }
}
