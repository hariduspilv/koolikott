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
import java.util.ArrayList;
import java.util.List;

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
import ee.hm.dop.model.LanguageString;
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
    public void parse() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/parse.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language french = new Language();
        french.setId(1L);
        french.setName("French");

        Language estonian = new Language();
        estonian.setId(2L);
        estonian.setName("Estonian");

        Tag tag = new Tag();
        tag.setId(325L);
        tag.setName("grammaire");

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);
        expect(tagService.getTagByName("grammaire")).andReturn(tag);

        LanguageString title1 = new LanguageString();
        title1.setLanguage(french);
        title1.setText("Subjonctif");

        LanguageString title2 = new LanguageString();
        title2.setLanguage(estonian);
        title2.setText("Les exercices du subjonctif.");

        List<LanguageString> titles = new ArrayList<>();
        titles.add(title1);
        titles.add(title2);

        LanguageString description1 = new LanguageString();
        description1.setLanguage(french);
        description1.setText("Exercice a completer");

        LanguageString description2 = new LanguageString();
        description2.setLanguage(estonian);
        description2.setText("Veebipõhised harjutused kahtleva kõneviisi kohta.");

        List<LanguageString> descriptions = new ArrayList<>();
        descriptions.add(description1);
        descriptions.add(description2);

        List<Tag> tags = new ArrayList<>();
        tags.add(tag);

        replay(languageService, tagService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, tagService);

        assertEquals("oai:ait.opetaja.ee:437556e69c7ee410b3ff27ad3eaec360219c3990", material.getRepositoryIdentifier());
        assertEquals(titles, material.getTitles());
        assertEquals(descriptions, material.getDescriptions());
        assertEquals(tags, material.getTags());
        assertEquals(french, material.getLanguage());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/metadata?id=437556e69c7ee410b3ff27ad3eaec360219c3990",
                material.getSource());
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

    private File getResourceAsFile(String resourcePath) throws URISyntaxException {
        URI resource = getClass().getClassLoader().getResource(resourcePath).toURI();
        return new File(resource);
    }
}
