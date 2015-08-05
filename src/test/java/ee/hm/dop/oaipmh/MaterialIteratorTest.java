package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.oaipmh.waramu.MaterialParserWaramu;

/**
 * Created by mart.laus on 23.07.2015.
 */
@RunWith(EasyMockRunner.class)
public class MaterialIteratorTest {

    @TestSubject
    private MaterialIterator materialIterator = new MaterialIterator();

    @Mock
    private ListIdentifiersConnector listIdentifiersConnector;

    @Mock
    private Iterator<Element> identifierIterator;

    @Mock
    private Element element;

    @Mock
    private NodeList nodeList;

    @Mock
    private Node node;

    @Mock
    private GetMaterialConnector getMaterialConnector;

    @Mock
    private Document document;

    @Test
    public void connect() throws Exception {
        MaterialIterator materialIterator = createMock(MaterialIterator.class);
        Repository repository = createMock(Repository.class);
        Iterator iterator = createMock(Iterator.class);

        expect(materialIterator.connect(repository)).andReturn(iterator);

        replay(materialIterator, repository, iterator);

        Iterator<Material> returnedIterator = materialIterator.connect(repository);

        verify(materialIterator, repository, iterator);

        assertEquals(returnedIterator, iterator);
    }

    @Test
    public void next() {
        MaterialIterator materialIterator = createMock(MaterialIterator.class);
        Material material = createMock(Material.class);

        expect(materialIterator.next()).andReturn(material);

        replay(materialIterator, material);

        Material newMaterial = materialIterator.next();

        verify(materialIterator, material);

        assertEquals(material, newMaterial);
    }

    @Test(expected = Exception.class)
    public void nextErrorInGettingMaterial() throws Exception {
        MaterialIterator materialIterator = createMock(MaterialIterator.class);
        Material material = createMock(Material.class);
        GetMaterialConnector getMaterialConnector = createMock(GetMaterialConnector.class);
        Repository repository = createMock(Repository.class);

        expect(materialIterator.next()).andReturn(material);
        expect(getMaterialConnector.getMaterial(repository, "id", "prefix")).andThrow(new Exception());

        replay(materialIterator, material, getMaterialConnector);

        Material newMaterial = materialIterator.next();
        getMaterialConnector.getMaterial(repository, "id", "prefix");

        verify(materialIterator, material, getMaterialConnector);

        assertEquals(material, newMaterial);
    }

    @Test(expected = NullPointerException.class)
    public void connectNullRepository() throws Exception {
        Repository repository = null;

        materialIterator.connect(repository);
    }

    @Test(expected = NullPointerException.class)
    public void connectWrongRepository() throws Exception {
        Repository repository = new Repository();
        repository.setBaseURL("test");
        repository.setSchema("test");

        Iterator<Material> iterator = materialIterator.connect(repository);
    }

    @Test
    public void connectCorrectRepo() throws Exception {
        Repository repository = getRepository();

        expect(listIdentifiersConnector.connect(repository.getBaseURL(), "oai_lom"))
                .andReturn(listIdentifiersConnector);
        expect(listIdentifiersConnector.iterator()).andReturn(identifierIterator);

        replay(listIdentifiersConnector);

        Iterator<Material> iterator = materialIterator.connect(repository);

        verify(listIdentifiersConnector);

        assertNotNull(iterator);
    }

    @Test(expected = NullPointerException.class)
    public void nextNull() {
        expect(identifierIterator.next()).andReturn(null);

        replay(identifierIterator);

        materialIterator.next();

        verify(identifierIterator);
    }

    @Test(expected = RuntimeException.class)
    public void nextNullDocument() throws Exception {
        Repository repository = getRepository();
        String metadataPrefix = "oai_lom";

        expect(identifierIterator.next()).andReturn(element);
        expect(element.getElementsByTagName("identifier")).andReturn(nodeList);
        expect(nodeList.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("identifier");
        expect(listIdentifiersConnector.connect(repository.getBaseURL(), metadataPrefix))
                .andReturn(listIdentifiersConnector);
        expect(listIdentifiersConnector.iterator()).andReturn(identifierIterator);
        expect(getMaterialConnector.getMaterial(repository, "identifier", metadataPrefix)).andReturn(null);

        replay(identifierIterator, element, nodeList, node, listIdentifiersConnector, getMaterialConnector);

        materialIterator.connect(repository);
        materialIterator.next();

        verify(identifierIterator, element, nodeList, node, listIdentifiersConnector, getMaterialConnector);
    }

    @Test(expected = ParseException.class)
    public void nextNullMaterial() throws Exception {
        Repository repository = getRepository();
        String metadataPrefix = "oai_lom";
        MaterialParserWaramu materialParserWaramu = new MaterialParserWaramu();

        expect(identifierIterator.next()).andReturn(element);
        expect(element.getElementsByTagName("identifier")).andReturn(nodeList);
        expect(nodeList.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("identifier");
        expect(listIdentifiersConnector.connect(repository.getBaseURL(), metadataPrefix))
                .andReturn(listIdentifiersConnector);
        expect(listIdentifiersConnector.iterator()).andReturn(identifierIterator);
        expect(getMaterialConnector.getMaterial(repository, "identifier", metadataPrefix)).andReturn(document);
        expect(materialParserWaramu.parse(document)).andReturn(null);

        replay(identifierIterator, element, nodeList, node, listIdentifiersConnector, getMaterialConnector,
                materialParserWaramu);

        materialIterator.connect(repository);

        materialIterator.next();

        verify(identifierIterator, element, nodeList, node, listIdentifiersConnector, getMaterialConnector,
                materialParserWaramu);
    }

    @Test
    public void nextMaterial() throws Exception {
        Repository repository = getRepository();
        String metadataPrefix = "oai_lom";
        Material material = new Material();
        MaterialParserWaramu materialParserWaramu = createMock(MaterialParserWaramu.class);

        expect(identifierIterator.next()).andReturn(element);
        expect(element.getElementsByTagName("identifier")).andReturn(nodeList);
        expect(nodeList.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("identifier");
        expect(listIdentifiersConnector.connect(repository.getBaseURL(), metadataPrefix))
                .andReturn(listIdentifiersConnector);
        expect(listIdentifiersConnector.iterator()).andReturn(identifierIterator);
        expect(getMaterialConnector.getMaterial(repository, "identifier", metadataPrefix)).andReturn(document);
        expect(materialParserWaramu.parse(document)).andReturn(material);

        replay(identifierIterator, element, nodeList, node, listIdentifiersConnector, getMaterialConnector,
                materialParserWaramu);

        materialIterator.setParser(materialParserWaramu);
        materialIterator.connect(repository);
        Material newMaterial = materialIterator.next();

        verify(identifierIterator, element, nodeList, node, listIdentifiersConnector, getMaterialConnector,
                materialParserWaramu);

        assertEquals(material, newMaterial);
    }

    private Repository getRepository() {
        Repository repository = new Repository();
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        repository.setSchema("waramu");
        return repository;
    }
}
