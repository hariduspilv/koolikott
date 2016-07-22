package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.oaipmh.waramu.MaterialParserWaramu;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@RunWith(EasyMockRunner.class)
public class MaterialIteratorTest {

    @TestSubject
    private MaterialIterator materialIterator = new MaterialIterator();

    @Mock
    private ListIdentifiersConnector listIdentifiersConnector;

    @Mock
    private Iterator<Element> identifierIterator;

    @Mock
    private Element header;

    @Mock
    private NodeList identifiers;

    @Mock
    private Node identifier;

    @Mock
    private GetMaterialConnector getMaterialConnector;

    @Mock
    private Document document;

    @Test(expected = NullPointerException.class)
    public void connectNullRepository() throws Exception {

        materialIterator.connect(null);
    }

    @Test
    public void connect() throws Exception {
        Repository repository = getRepository();

        expect(listIdentifiersConnector.connect(repository.getBaseURL(), repository.getLastSynchronization(),
                repository.getMetadataPrefix())).andReturn(listIdentifiersConnector);
        expect(listIdentifiersConnector.iterator()).andReturn(identifierIterator);

        replayAll();

        Iterator<Material> iterator = materialIterator.connect(repository);

        verifyAll();

        assertNotNull(iterator);
    }

    @Test
    public void nextNull() {
        expect(identifierIterator.next()).andReturn(null);

        replayAll();

        try {
            materialIterator.next();
            fail("Exception expected");
        } catch (NullPointerException e) {
            // Ok
        }

        verifyAll();
    }

    @Test
    public void nextNullDocument() throws Exception {
        Repository repository = getRepository();

        expectsForNext(repository, null);
        expect(getMaterialConnector.getMaterial(repository, "identifier", repository.getMetadataPrefix()))
                .andReturn(null);

        replayAll();

        materialIterator.connect(repository);

        try {
            materialIterator.next();
            fail("Exception was expected.");
        } catch (RuntimeException e) {
            // as expected
        }

        verifyAll();
    }

    @Test
    public void nextNullMaterial() throws Exception {
        Repository repository = getRepository();

        expectsForNext(repository, null);
        expect(getMaterialConnector.getMaterial(repository, "identifier", repository.getMetadataPrefix()))
                .andReturn(document);

        MaterialParserWaramu materialParserWaramu = createMock(MaterialParserWaramu.class);
        String errorMessage = "Very bad code here :)";
        expect(materialParserWaramu.parse(document)).andThrow(new RuntimeException(errorMessage));

        replayAll(materialParserWaramu);

        materialIterator.setParser(materialParserWaramu);
        materialIterator.connect(repository);

        try {
            materialIterator.next();
            fail("Exception was expected.");
        } catch (Exception e) {
            assertEquals(errorMessage, e.getCause().getMessage());
        }

        verifyAll(materialParserWaramu);
    }

    @Test
    public void nextMaterial() throws Exception {
        Repository repository = getRepository();
        Material material = new Material();
        MaterialParserWaramu materialParserWaramu = createMock(MaterialParserWaramu.class);

        expectsForNext(repository, null);
        expect(getMaterialConnector.getMaterial(repository, "identifier", repository.getMetadataPrefix()))
                .andReturn(document);
        expect(materialParserWaramu.parse(document)).andReturn(material);

        replayAll(materialParserWaramu);

        materialIterator.setParser(materialParserWaramu);
        materialIterator.connect(repository);
        Material newMaterial = materialIterator.next();

        verifyAll(materialParserWaramu);

        assertEquals(material, newMaterial);
    }

    @Test
    public void nextDeletedMaterial() throws Exception {
        Repository repository = getRepository();

        expectsForNext(repository, "deleted");

        replayAll();

        materialIterator.connect(repository);
        Material newMaterial = materialIterator.next();

        verifyAll();

        assertTrue(newMaterial.isDeleted());
        assertEquals("identifier", newMaterial.getRepositoryIdentifier());
    }

    private void expectsForNext(Repository repository, String status) throws Exception {
        expect(identifierIterator.next()).andReturn(header);
        expect(header.getElementsByTagName("identifier")).andReturn(identifiers);
        expect(header.getAttribute("status")).andReturn(status);
        expect(identifiers.item(0)).andReturn(identifier);
        expect(identifier.getTextContent()).andReturn("identifier");
        expect(listIdentifiersConnector.connect(repository.getBaseURL(), repository.getLastSynchronization(),
                repository.getMetadataPrefix())).andReturn(listIdentifiersConnector);
        expect(listIdentifiersConnector.iterator()).andReturn(identifierIterator);
    }

    private Repository getRepository() {
        Repository repository = new Repository();
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        repository.setSchema("waramu");
        repository.setLastSynchronization(new DateTime("2015-01-27T08:14:27"));
        repository.setMetadataPrefix("oai_estcore");
        return repository;
    }

    private void replayAll(Object... mocks) {
        replay(listIdentifiersConnector, identifierIterator, header, identifiers, identifier, getMaterialConnector,
                document);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(listIdentifiersConnector, identifierIterator, header, identifiers, identifier, getMaterialConnector,
                document);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
