package ee.hm.dop.service.synchronizer.oaipmh;

import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

@RunWith(EasyMockRunner.class)
public class IdentifierIteratorTest {

    public static final String RESUMPTION_TOKEN = "token";
    public static final String BASE_URL = "repositoryURL";

    private IdentifierIterator getIdentifierIterator(NodeList headers, String baseURL, String resumptionToken)
            throws NoSuchMethodException {
        Constructor<IdentifierIterator> constructor = IdentifierIterator.class.getDeclaredConstructor(NodeList.class,
                String.class, String.class);

        Method newListIdentifier = IdentifierIterator.class.getDeclaredMethod("newListIdentifier", String.class,
                String.class);

        return createMockBuilder(IdentifierIterator.class).addMockedMethod(newListIdentifier)
                .withConstructor(constructor).withArgs(headers, baseURL, resumptionToken).createMock();
    }

    @Test
    public void next() throws Exception {
        Element element = createMock(Element.class);
        NodeList nodeList = createMock(NodeList.class);
        expect(nodeList.item(0)).andReturn(element);

        replay(element, nodeList);

        Node next = getIdentifierIterator(nodeList, null, null).next();

        verify(element, nodeList);
        assertSame(element, next);
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
        } catch (NoSuchElementException e) {
            // ignore
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
        NodeList nodeList1 = createMock(NodeList.class);
        expect(nodeList1.getLength()).andReturn(0); // same as index ==
                                                    // headers.length()

        Element element = createMock(Element.class);

        NodeList nodeList2 = createMock(NodeList.class);
        expect(nodeList2.getLength()).andReturn(1);
        expect(nodeList2.item(0)).andReturn(element);

        element.normalize();

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList2);

        ListIdentifiers listIdentifiers = createMock(ListIdentifiers.class);
        expect(listIdentifiers.getDocument()).andReturn(document);
        expect(listIdentifiers.getResumptionToken()).andReturn(RESUMPTION_TOKEN);

        IdentifierIterator identifierIterator = getIdentifierIterator(nodeList1, BASE_URL, RESUMPTION_TOKEN);
        expect(identifierIterator.newListIdentifier(BASE_URL, RESUMPTION_TOKEN)).andReturn(listIdentifiers);

        replay(nodeList1, nodeList2, element, document, listIdentifiers, identifierIterator);

        boolean result = identifierIterator.hasNext();
        // This is needed to verify that correct parameters were passed to
        // IdentifierIterator constructor
        Element next = identifierIterator.next();

        verify(nodeList1, nodeList2, element, document, listIdentifiers, identifierIterator);

        assertTrue(result);
        assertSame(element, next);
    }

    @Test
    public void hasNextFirstHeadersAllConsumedThenGetNewHeadersNull() throws Exception {
        NodeList nodeList1 = createMock(NodeList.class);
        expect(nodeList1.getLength()).andReturn(0).times(2); // same as index ==
                                                             // headers.length()

        IdentifierIterator identifierIterator = getIdentifierIterator(nodeList1, BASE_URL, RESUMPTION_TOKEN);
        expect(identifierIterator.newListIdentifier(BASE_URL, RESUMPTION_TOKEN)).andReturn(null);

        replay(nodeList1, identifierIterator);

        boolean result = identifierIterator.hasNext();

        verify(nodeList1, identifierIterator);

        assertFalse(result);
    }

    @Test
    public void hasNextFirstHeadersAllConsumedAndNewHeadersEmpty() throws Exception {
        NodeList nodeList1 = createMock(NodeList.class);
        expect(nodeList1.getLength()).andReturn(0); // same as index ==
                                                    // headers.length()

        NodeList nodeList2 = createMock(NodeList.class);
        expect(nodeList2.getLength()).andReturn(0);

        Element element = createMock(Element.class);
        element.normalize();

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList2);

        ListIdentifiers listIdentifiers = createMock(ListIdentifiers.class);
        expect(listIdentifiers.getDocument()).andReturn(document);
        expect(listIdentifiers.getResumptionToken()).andReturn(RESUMPTION_TOKEN);

        IdentifierIterator identifierIterator = getIdentifierIterator(nodeList1, BASE_URL, RESUMPTION_TOKEN);
        expect(identifierIterator.newListIdentifier(BASE_URL, RESUMPTION_TOKEN)).andReturn(listIdentifiers);

        replay(nodeList1, nodeList2, element, document, listIdentifiers, identifierIterator);

        boolean result = identifierIterator.hasNext();

        verify(nodeList1, nodeList2, element, document, listIdentifiers, identifierIterator);

        assertFalse(result);
    }

    /**
     * This tests the flow while hasNext, get next. It does not care about
     * returning the correct nodes, it is tested in the other tests.
     */
    @Test
    public void consumeAll() throws Exception {

        Element element = createMock(Element.class);

        NodeList nodeList1 = createMock(NodeList.class);
        expect(nodeList1.getLength()).andReturn(3).times(4);
        expect(nodeList1.item(0)).andReturn(element);
        expect(nodeList1.item(1)).andReturn(element);
        expect(nodeList1.item(2)).andReturn(element);

        NodeList nodeList2 = createMock(NodeList.class);
        expect(nodeList2.getLength()).andReturn(2).times(4);
        expect(nodeList2.item(0)).andReturn(element);
        expect(nodeList2.item(1)).andReturn(element);

        element.normalize();

        Document document = createMock(Document.class);
        expect(document.getDocumentElement()).andReturn(element);
        expect(document.getElementsByTagName("header")).andReturn(nodeList2);

        ListIdentifiers listIdentifiers = createMock(ListIdentifiers.class);
        expect(listIdentifiers.getDocument()).andReturn(document);
        expect(listIdentifiers.getResumptionToken()).andReturn(RESUMPTION_TOKEN);

        IdentifierIterator identifierIterator = getIdentifierIterator(nodeList1, BASE_URL, RESUMPTION_TOKEN);
        expect(identifierIterator.newListIdentifier(BASE_URL, RESUMPTION_TOKEN)).andReturn(listIdentifiers);
        expect(identifierIterator.newListIdentifier(BASE_URL, RESUMPTION_TOKEN)).andReturn(null);

        replay(nodeList1, nodeList2, element, document, listIdentifiers, identifierIterator);

        while (identifierIterator.hasNext()) {
            identifierIterator.next();
        }

        verify(nodeList1, nodeList2, element, document, listIdentifiers, identifierIterator);
    }
}