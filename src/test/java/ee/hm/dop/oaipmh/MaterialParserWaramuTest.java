package ee.hm.dop.oaipmh;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

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
    public void parse() throws ParseException, ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File(workingDir + File.separator + "src" + File.separator + "test" + File.separator
                + "resources" + File.separator + "oaipmh" + File.separator + "parse.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        expect(languageService.getLanguage("fr")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("et")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("fren")).andReturn(language);
        expect(tagService.getTagByName("grammaire")).andReturn(tag);

        replay(languageService, tagService);

        Document doc = dBuilder.parse(fXmlFile);
        materialParser.parse(doc);

        verify(languageService, tagService);
    }

    @Test
    public void parseNullTitle() throws ParserConfigurationException, IOException, SAXException {
        String errorMessage = "Error in parsing Material title";

        File fXmlFile = new File(workingDir + File.separator + "src" + File.separator + "test" + File.separator
                + "resources" + File.separator + "oaipmh" + File.separator + "parseNullTitle.xml");
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
    public void parseNullLanguage() throws ParserConfigurationException, IOException, SAXException {
        String errorMessage = "Error in parsing Material language";

        File fXmlFile = new File(workingDir + File.separator + "src" + File.separator + "test" + File.separator
                + "resources" + File.separator + "oaipmh" + File.separator + "parseNullLanguage.xml");
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
    public void parseNullDescriptions() throws ParserConfigurationException, IOException, SAXException {
        String errorMessage = "Error in parsing Material descriptions";

        File fXmlFile = new File(workingDir + File.separator + "src" + File.separator + "test" + File.separator
                + "resources" + File.separator + "oaipmh" + File.separator + "parseNullDescriptions.xml");
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
    public void parseNullSource() throws ParserConfigurationException, IOException, SAXException {
        String errorMessage = "Material has more or less than one source, can't be mapped.";

        File fXmlFile = new File(workingDir + File.separator + "src" + File.separator + "test" + File.separator
                + "resources" + File.separator + "oaipmh" + File.separator + "parseNullSource.xml");
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
