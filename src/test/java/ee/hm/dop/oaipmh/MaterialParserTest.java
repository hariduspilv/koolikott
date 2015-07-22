package ee.hm.dop.oaipmh;

import static junit.framework.Assert.assertSame;
import static junit.framework.TestCase.assertNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Material;

/**
 * Created by mart.laus on 17.07.2015.
 */
public class MaterialParserTest {

    @Test
    public void parseXMLisNull() {
        MaterialParser materialParser = createMock(MaterialParser.class);
        Document document = createMock(Document.class);

        expect(materialParser.parseXMLtoMaterial(document)).andReturn(null);

        replay(materialParser, document);

        Material material = materialParser.parseXMLtoMaterial(document);

        verify(materialParser);

        assertNull(material);
    }

    @Test
    public void parseXMLNullLomElement() {
        MaterialParser materialParser = createMock(MaterialParser.class);
        Document document = createMock(Document.class);
        NodeList nodeList = createMock(NodeList.class);

        expect(materialParser.parseXMLtoMaterial(document)).andReturn(null);
        expect(document.getElementsByTagName("lom")).andReturn(null);

        replay(materialParser, document, nodeList);

        Material material = materialParser.parseXMLtoMaterial(document);
        NodeList nList = document.getElementsByTagName("lom");

        verify(materialParser, document, nodeList);

        assertNull(nList);
        assertNull(material);
    }

    @Test
    public void parseXMLtoMaterial() {
        MaterialParser materialParser = createMock(MaterialParser.class);
        Document document = createMock(Document.class);
        Material material = createMock(Material.class);

        expect(materialParser.parseXMLtoMaterial(document)).andReturn(material);

        replay(materialParser, document, material);

        Material returnedMaterial = materialParser.parseXMLtoMaterial(document);

        verify(materialParser);

        assertSame(returnedMaterial, material);
    }

}
