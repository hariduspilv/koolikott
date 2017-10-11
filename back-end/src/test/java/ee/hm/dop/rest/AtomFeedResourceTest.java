package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.enums.LanguageC;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AtomFeedResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_FEED = "%1$s/feed?lang=%1$s";

    @Test
    public void anybody_can_ask_for_atom_feed_in_estonian() throws Exception {
        Response response = getFeed(LanguageC.EST);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String result = response.readEntity(String.class);
        assertTrue(!result.isEmpty());

        Document doc = parseFeed(result);
        assertEquals("e-koolikott:et", doc.getElementsByTagName("id").item(0).getTextContent());
        assertEquals("feed", doc.getDocumentElement().getTagName());
    }

    @Test
    public void anybody_can_ask_for_atom_feed_in_english() throws Exception {
        Response response = getFeed(LanguageC.ENG);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String result = response.readEntity(String.class);
        assertTrue(!result.isEmpty());

        Document doc = parseFeed(result);
        assertEquals("e-koolikott:en", doc.getElementsByTagName("id").item(0).getTextContent());
        assertEquals("feed", doc.getDocumentElement().getTagName());
    }

    @Test
    public void anybody_can_ask_for_atom_feed_in_russian() throws Exception {
        Response response = getFeed(LanguageC.RUS);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String result = response.readEntity(String.class);
        assertTrue(!result.isEmpty());

        Document doc = parseFeed(result);
        assertEquals("e-koolikott:ru", doc.getElementsByTagName("id").item(0).getTextContent());
        assertEquals("feed", doc.getDocumentElement().getTagName());
    }

    private Response getFeed(String language) {
        return doGet(format(GET_FEED, language), MediaType.APPLICATION_ATOM_XML_TYPE);
    }

    private Document parseFeed(String result) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(new InputSource(new StringReader(result)));
    }
}