package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.enums.LanguageC;
import org.apache.abdera.model.Feed;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class AtomFeedResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_FEED = "%1$s/feed?lang=%1$s";

    @Ignore
    @Test
    public void anybody_can_ask_for_atom_feed_in_estonian() throws Exception {
        Response response = getFeed(LanguageC.EST);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String result = response.readEntity(String.class);
        assertTrue(!result.isEmpty());
    }

    @Ignore
    @Test
    public void anybody_can_ask_for_atom_feed_in_english() throws Exception {
        Response response = getFeed(LanguageC.ENG);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String result = response.readEntity(String.class);
        assertTrue(!result.isEmpty());
    }

    @Ignore
    @Test
    public void anybody_can_ask_for_atom_feed_in_russian() throws Exception {
        Response response = getFeed(LanguageC.RUS);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String result = response.readEntity(String.class);
        assertTrue(!result.isEmpty());
    }

    private Response getFeed(String language) {
        return doGet(format(GET_FEED, language), MediaType.APPLICATION_ATOM_XML_TYPE);
    }
}