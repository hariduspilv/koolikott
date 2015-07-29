package ee.hm.dop.oaipmh;

import static junit.framework.Assert.assertSame;
import static junit.framework.TestCase.assertNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.text.ParseException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Material;
import ee.hm.dop.oaipmh.waramu.MaterialParserWaramu;

/**
 * Created by mart.laus on 17.07.2015.
 */
public class MaterialParserWaramuTest {

    @Test
    public void parseXMLisNull() throws ParseException, ee.hm.dop.oaipmh.ParseException {
        MaterialParserWaramu materialParser = createMock(MaterialParserWaramu.class);
        Document document = createMock(Document.class);

        expect(materialParser.parse(document)).andReturn(null);

        replay(materialParser, document);

        Material material = materialParser.parse(document);

        verify(materialParser);

        assertNull(material);
    }

    @Test
    public void parseXMLNullLomElement() throws ParseException, ee.hm.dop.oaipmh.ParseException {
        MaterialParserWaramu materialParser = createMock(MaterialParserWaramu.class);
        Document document = createMock(Document.class);
        NodeList nodeList = createMock(NodeList.class);

        expect(materialParser.parse(document)).andReturn(null);
        expect(document.getElementsByTagName("lom")).andReturn(null);

        replay(materialParser, document, nodeList);

        Material material = materialParser.parse(document);
        NodeList nList = document.getElementsByTagName("lom");

        verify(materialParser, document, nodeList);

        assertNull(nList);
        assertNull(material);
    }

    @Test
    public void parseXMLtoMaterial() throws ParseException, ee.hm.dop.oaipmh.ParseException {
        MaterialParserWaramu materialParser = createMock(MaterialParserWaramu.class);
        Document document = createMock(Document.class);
        Material material = createMock(Material.class);

        expect(materialParser.parse(document)).andReturn(material);

        replay(materialParser, document, material);

        Material returnedMaterial = materialParser.parse(document);

        verify(materialParser);

        assertSame(returnedMaterial, material);
    }

}
