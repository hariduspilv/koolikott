package ee.hm.dop.oaipmh.estcore;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;

/**
 * Created by mart on 6.11.15.
 */
@RunWith(EasyMockRunner.class)
public class MaterialParserEstCoreTest {

    @TestSubject
    private MaterialParserEstCore materialParser = new MaterialParserEstCore();

    @Mock
    private LanguageService languageService;

    @Mock
    private AuthorService authorService;

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

        File fXmlFile = getResourceAsFile("oaipmh/estcore/parseEstcore.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language english = new Language();
        english.setId(1L);
        english.setName("English");

        Language estonian = new Language();
        estonian.setId(2L);
        estonian.setName("Estonian");

        Author author1 = new Author();
        author1.setName("Jonathan");
        author1.setSurname("Doe");

        Author author2 = new Author();
        author2.setName("Andrew");
        author2.setSurname("Balaam");

        LanguageString description1 = new LanguageString();
        description1.setLanguage(english);
        description1.setText("description 1");

        LanguageString description2 = new LanguageString();
        description2.setLanguage(estonian);
        description2.setText("description 2");

        expect(languageService.getLanguage("en")).andReturn(english).times(3);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(authorService.getAuthorByFullName(author1.getName(), author1.getSurname())).andReturn(author1);
        expect(authorService.getAuthorByFullName(author2.getName(), author2.getSurname())).andReturn(author2);

        LanguageString title1 = new LanguageString();
        title1.setLanguage(english);
        title1.setText("first title");

        LanguageString title2 = new LanguageString();
        title2.setLanguage(estonian);
        title2.setText("teine pealkiri");

        List<LanguageString> titles = new ArrayList<>();
        titles.add(title1);
        titles.add(title2);

        List<Author> authors = new ArrayList<>();
        authors.add(author1);
        authors.add(author2);

        List<LanguageString> descriptions = new ArrayList<>();
        descriptions.add(description1);
        descriptions.add(description2);

        replay(languageService, authorService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, authorService);

        assertEquals(titles, material.getTitles());
        assertEquals("https://oxygen.netgroupdigital.com/rest/repoMaterialSource", material.getSource());
        assertEquals(english, material.getLanguage());
        assertEquals(authors, material.getAuthors());
        assertEquals(descriptions, material.getDescriptions());
    }

    private File getResourceAsFile(String resourcePath) throws URISyntaxException {
        URI resource = getClass().getClassLoader().getResource(resourcePath).toURI();
        return new File(resource);
    }
}
