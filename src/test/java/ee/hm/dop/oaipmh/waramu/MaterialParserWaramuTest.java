package ee.hm.dop.oaipmh.waramu;

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

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;

@RunWith(EasyMockRunner.class)
public class MaterialParserWaramuTest {

    @TestSubject
    private MaterialParserWaramu materialParser = new MaterialParserWaramu();

    @Mock
    private LanguageService languageService;

    @Mock
    private TagService tagService;

    @Mock
    private ResourceTypeService resourceTypeService;

    @Mock
    private TaxonService taxonService;

    @Mock
    private AuthorService authorService;

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
        File fXmlFile = getResourceAsFile("oaipmh/waramu/parseWaramu.xml");

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

        ResourceType resourceType1 = new ResourceType();
        resourceType1.setId(444L);
        resourceType1.setName("WEBSITE");

        ResourceType resourceType2 = new ResourceType();
        resourceType2.setId(555L);
        resourceType2.setName("COURSE");

        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setName("BASICEDUCATION");

        Author author = new Author();
        author.setName("Andrew");
        author.setSurname("Balaam");

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);
        expect(tagService.getTagByName("grammaire")).andReturn(tag);
        expect(resourceTypeService.getResourceTypeByName(resourceType1.getName())).andReturn(resourceType1);
        expect(resourceTypeService.getResourceTypeByName(resourceType2.getName())).andReturn(resourceType2);
        expect(taxonService.getTaxonByWaramuName("COMPULSORYEDUCATION", EducationalContext.class)).andReturn(
                educationalContext);
        expect(authorService.getAuthorByFullName(author.getName(), author.getSurname())).andReturn(author);

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

        List<ResourceType> resourceTypes = new ArrayList<>();
        resourceTypes.add(resourceType1);
        resourceTypes.add(resourceType2);

        List<EducationalContext> educationalContexts = new ArrayList<>();
        educationalContexts.add(educationalContext);

        List<Author> authors = new ArrayList<>();
        authors.add(author);

        replay(languageService, tagService, resourceTypeService, taxonService, authorService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, tagService, resourceTypeService, taxonService, authorService);

        assertEquals("oai:ait.opetaja.ee:437556e69c7ee410b3ff27ad3eaec360219c3990", material.getRepositoryIdentifier());
        assertEquals(titles, material.getTitles());
        assertEquals(descriptions, material.getDescriptions());
        assertEquals(tags, material.getTags());
        assertEquals(french, material.getLanguage());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/metadata?id=437556e69c7ee410b3ff27ad3eaec360219c3990",
                material.getSource());
        assertEquals(resourceTypes, material.getResourceTypes());
        assertEquals(educationalContexts, material.getTaxons());
        assertEquals(authors, material.getAuthors());
        assertEquals(4, material.getTargetGroups().size());
    }

    @Test
    public void parseWithNewAuthor() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/waramu/parseNewAuthor.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language french = new Language();
        french.setId(1L);
        french.setName("French");

        Language estonian = new Language();
        estonian.setId(2L);
        estonian.setName("Estonian");

        Author author = new Author();
        author.setName("Author");
        author.setSurname("Supernew");

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);
        expect(authorService.getAuthorByFullName(author.getName(), author.getSurname())).andReturn(null);
        expect(authorService.createAuthor(author.getName(), author.getSurname())).andReturn(author);
        List<Author> authors = new ArrayList<>();
        authors.add(author);

        replay(languageService, authorService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, authorService);

        assertEquals("oai:ait.opetaja.ee:437556e69c7ee410b3ff27ad3eaec360219c3990", material.getRepositoryIdentifier());
        assertEquals(french, material.getLanguage());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/metadata?id=437556e69c7ee410b3ff27ad3eaec360219c3990",
                material.getSource());
        assertEquals(authors, material.getAuthors());
    }

    @Test
    public void parseWithNoAuthorData() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/waramu/parseNoAuthor.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language french = new Language();
        french.setId(1L);
        french.setName("French");

        Language estonian = new Language();
        estonian.setId(2L);
        estonian.setName("Estonian");

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);

        replay(languageService, authorService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, authorService);

        assertEquals("oai:ait.opetaja.ee:437556e69c7ee410b3ff27ad3eaec360219c3990", material.getRepositoryIdentifier());
        assertEquals(french, material.getLanguage());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/metadata?id=437556e69c7ee410b3ff27ad3eaec360219c3990",
                material.getSource());
    }

    @Test
    public void parseNullTitle() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        String errorMessage = "Error in parsing Material title";

        URI resource = getClass().getClassLoader().getResource("oaipmh/waramu/parseNullTitle.xml").toURI();
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
    public void parseNullSource() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        String errorMessage = "Error parsing document source.";

        URI resource = getClass().getClassLoader().getResource("oaipmh/waramu/parseNullSource.xml").toURI();
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

    private File getResourceAsFile(String resourcePath) throws URISyntaxException {
        URI resource = getClass().getClassLoader().getResource(resourcePath).toURI();
        return new File(resource);
    }
}
