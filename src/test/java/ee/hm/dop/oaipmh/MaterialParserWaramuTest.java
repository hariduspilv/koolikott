package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Tag;
import ee.hm.dop.oaipmh.waramu.MaterialParserWaramu;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.TagService;

@RunWith(EasyMockRunner.class)
public class MaterialParserWaramuTest {

    @TestSubject
    private MaterialParserWaramu materialParser = new MaterialParserWaramu();

    @Mock
    private LanguageService languageService;

    @Mock
    private TagService tagService;

    private String workingDir = System.getProperty("user.dir");
    private Language language = new Language();
    private Tag tag = new Tag();

    @Test(expected = ee.hm.dop.oaipmh.ParseException.class)
    public void parseXMLisNull() throws ParseException {
        materialParser.parse(null);
    }

    @Test(expected = ee.hm.dop.oaipmh.ParseException.class)
    public void parseDocumentIsEmpty() throws ParseException {
        Document document = createMock(Document.class);
        materialParser.parse(document);
    }

    @Test
    public void parse()
            throws ParseException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
        URI resource = getClass().getClassLoader().getResource("oaipmh/parse.xml").toURI();
        File fXmlFile = new File(resource);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        expect(languageService.getLanguage("fr")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("et")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("fren")).andReturn(language);
        expect(tagService.getTagByName("grammaire")).andReturn(tag);

        replay(languageService, tagService);

        Document doc = dBuilder.parse(fXmlFile);
        Material result = materialParser.parse(doc);

        verify(languageService, tagService);

        assertEquals("Subjonctif", result.getTitles().get(0).getText());
        assertEquals("Les exercices du subjonctif.", result.getTitles().get(1).getText());
        assertEquals("Exercice a completer", result.getDescriptions().get(0).getText());
        assertEquals("Veebipõhised harjutused kahtleva kõneviisi kohta.", result.getDescriptions().get(1).getText());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/metadata?id=437556e69c7ee410b3ff27ad3eaec360219c3990", result.getSource());
        assertEquals(1, result.getTags().size());
    }

    @Test
    public void parseNullTitle() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        String errorMessage = "Error in parsing Material title";

        URI resource = getClass().getClassLoader().getResource("oaipmh/parseNullTitle.xml").toURI();
        File fXmlFile = new File(resource);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        expect(languageService.getLanguage("fr")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("et")).andReturn(language).anyTimes();

        replay(languageService);

        Document doc = dBuilder.parse(fXmlFile);

        try {
            materialParser.parse(doc);
            fail("Exception expected.");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(languageService);
    }

    @Test
    public void parseNullLanguage() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        String errorMessage = "Error in parsing Material language";

        URI resource = getClass().getClassLoader().getResource("oaipmh/parseNullLanguage.xml").toURI();
        File fXmlFile = new File(resource);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        expect(languageService.getLanguage("fr")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("et")).andReturn(language).anyTimes();

        replay(languageService);

        Document doc = dBuilder.parse(fXmlFile);

        try {
            materialParser.parse(doc);
            fail("Exception expected.");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(languageService);
    }

    @Test
    public void parseNullDescriptions()
            throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        String errorMessage = "Error in parsing Material descriptions";

        URI resource = getClass().getClassLoader().getResource("oaipmh/parseNullDescriptions.xml").toURI();
        File fXmlFile = new File(resource);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        expect(languageService.getLanguage("fr")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("et")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("fren")).andReturn(language);

        replay(languageService);

        Document doc = dBuilder.parse(fXmlFile);

        try {
            materialParser.parse(doc);
            fail("Exception expected.");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(languageService);
    }

    @Test
    public void parseNullSource() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        String errorMessage = "Material has more or less than one source, can't be mapped.";

        URI resource = getClass().getClassLoader().getResource("oaipmh/parseNullSource.xml").toURI();
        File fXmlFile = new File(resource);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        expect(languageService.getLanguage("fr")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("et")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("fren")).andReturn(language);

        replay(languageService);

        Document doc = dBuilder.parse(fXmlFile);

        try {
            materialParser.parse(doc);
            fail("Exception expected.");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(languageService);
    }
}
