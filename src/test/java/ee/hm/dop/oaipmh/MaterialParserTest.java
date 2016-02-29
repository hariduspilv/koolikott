package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.LanguageService;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by mart on 10.11.15.
 */

@RunWith(EasyMockRunner.class)
public class MaterialParserTest {

    @TestSubject
    private MaterialParser materialParser = new MaterialParserImpl();

    @Mock
    private LanguageService languageService;

    @Mock
    private Node node;

    @Mock
    private NodeList nodeList;

    @Test
    public void getLanguageStringsEmptyNode() {
        expect(node.getChildNodes()).andReturn(nodeList);
        expect(nodeList.getLength()).andReturn(0);

        replay(languageService, node, nodeList);

        List<LanguageString> languageStrings = materialParser.getLanguageStrings(node, languageService);

        verify(languageService, node, nodeList);

        assertEquals(0, languageStrings.size());
    }

    @Test
    public void getLanguageStringsNoStrings() {
        expect(node.getChildNodes()).andReturn(nodeList);
        expect(nodeList.getLength()).andReturn(1).times(2);
        expect(nodeList.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("");

        replay(languageService, node, nodeList);

        List<LanguageString> languageStrings = materialParser.getLanguageStrings(node, languageService);

        verify(languageService, node, nodeList);

        assertEquals(0, languageStrings.size());
    }

    @Test
    public void getLanguageStringsNoAttributes() {
        expect(node.getChildNodes()).andReturn(nodeList);
        expect(nodeList.getLength()).andReturn(1).times(2);
        expect(nodeList.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("someValue");
        expect(node.hasAttributes()).andReturn(false);

        replay(languageService, node, nodeList);

        List<LanguageString> languageStrings = materialParser.getLanguageStrings(node, languageService);

        verify(languageService, node, nodeList);

        assertEquals(1, languageStrings.size());
        assertNull(languageStrings.get(0).getLanguage());
        assertEquals("someValue", languageStrings.get(0).getText());
    }

    @Test
    public void getLanguageStringsNoLanguage() {
        NamedNodeMap namedNodeMap = createMock(NamedNodeMap.class);

        expect(node.getChildNodes()).andReturn(nodeList);
        expect(nodeList.getLength()).andReturn(1).times(2);
        expect(nodeList.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("someValue").times(1);
        expect(node.hasAttributes()).andReturn(true);
        expect(node.getAttributes()).andReturn(namedNodeMap);
        expect(namedNodeMap.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("");
        expect(languageService.getLanguage("")).andReturn(null);

        replay(languageService, node, nodeList, namedNodeMap);

        List<LanguageString> languageStrings = materialParser.getLanguageStrings(node, languageService);

        verify(languageService, node, nodeList, namedNodeMap);

        assertEquals(1, languageStrings.size());
        assertNull(languageStrings.get(0).getLanguage());
        assertEquals("someValue", languageStrings.get(0).getText());

    }

    @Test
    public void getLanguageStrings() {
        NamedNodeMap namedNodeMap = createMock(NamedNodeMap.class);

        expect(node.getChildNodes()).andReturn(nodeList);
        expect(nodeList.getLength()).andReturn(1).times(2);
        expect(nodeList.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("someValue").times(1);
        expect(node.hasAttributes()).andReturn(true);
        expect(node.getAttributes()).andReturn(namedNodeMap);
        expect(namedNodeMap.item(0)).andReturn(node);
        expect(node.getTextContent()).andReturn("pt-br");
        expect(languageService.getLanguage("pt")).andReturn(new Language());

        replay(languageService, node, nodeList, namedNodeMap);

        List<LanguageString> languageStrings = materialParser.getLanguageStrings(node, languageService);

        verify(languageService, node, nodeList, namedNodeMap);

        assertEquals(1, languageStrings.size());
        assertNotNull(languageStrings.get(0).getLanguage());
        assertEquals("someValue", languageStrings.get(0).getText());
    }

    private class MaterialParserImpl extends MaterialParser {

        @Override
        protected void setContributorsData(Material material, Document doc) {
        }

        @Override
        protected void setTags(Material material, Document doc) {
        }

        @Override
        protected void setDescriptions(Material material, Document doc) {
        }

        @Override
        protected void setLanguage(Material material, Document doc) {
        }

        @Override
        protected void setTitles(Material material, Document doc) throws ParseException {
        }

        @Override
        protected String getPathToContext() {
            return null;
        }

        @Override
        protected String getPathToResourceType() {
            return null;
        }

        @Override
        protected String getPathToLocation() {
            return null;
        }

        @Override
        protected String getPathToContribute() {
            return null;
        }

        @Override
        protected Taxon setEducationalContext(Node node) {
            return null;
        }

        @Override
        protected Taxon setDomain(Node node, Taxon lastTaxon) {
            return null;
        }

        @Override
        protected Taxon getTaxon(String context, Class level) {
            return null;
        }

        @Override
        protected void setIsPaid(Material material, Document doc) {
        }

        @Override
        protected String getPathToTargetGroups() {
            return null;
        }

        @Override
        protected String getPathToCurriculumLiterature() {
            return null;
        }

        @Override
        protected void setTargetGroups(Material material, Document doc) {
        }

        @Override
        protected void setPicture(Material material, Document doc) {
        }

        @Override
        protected void setCrossCurricularThemes(Material material, Document doc) {

        }

        @Override
        protected void setKeyCompetences(Material material, Document doc) {

        }

        @Override
        protected String getPathToClassification() {
            return null;
        }
    }
}
