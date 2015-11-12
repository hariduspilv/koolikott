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

import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.TagService;

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
    private TagService tagService;

    @Mock
    private ResourceTypeService resourceTypeService;

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

        expect(languageService.getLanguage("en")).andReturn(english).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian);

        LanguageString title1 = new LanguageString();
        title1.setLanguage(english);
        title1.setText("first title");

        LanguageString title2 = new LanguageString();
        title2.setLanguage(estonian);
        title2.setText("teine pealkiri");

        List<LanguageString> titles = new ArrayList<>();
        titles.add(title1);
        titles.add(title2);

        replay(languageService, tagService, resourceTypeService, authorService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, tagService, resourceTypeService, authorService);

        assertEquals(titles, material.getTitles());
        assertEquals("https://oxygen.netgroupdigital.com/rest/repoMaterialSource", material.getSource());
        assertEquals(english, material.getLanguage());


    }

    private File getResourceAsFile(String resourcePath) throws URISyntaxException {
        URI resource = getClass().getClassLoader().getResource(resourcePath).toURI();
        return new File(resource);
    }
}
