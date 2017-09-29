package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import org.junit.Test;

public class TranslationResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void downloadRussianTranslation() {
        Map<String, String> translations = doGet("translation?lang=rus", map());

        assertEquals(3, translations.size());
        assertEquals("FOO сообщение", translations.get("FOO"));
        assertEquals("Эстонский язык", translations.get("Estonian"));
        assertEquals("русский язык", translations.get("Russian"));
    }

    @Test
    public void downloadEstonianTranslation() {
        Map<String, String> translations = doGet("translation?lang=est", map());

        assertEquals(3, translations.size());
        assertEquals("FOO sõnum", translations.get("FOO"));
        assertEquals("Eesti keeles", translations.get("Estonian"));
        assertEquals("Vene keel", translations.get("Russian"));
    }

    @Test
    public void downloadTranslationWithoutParam() {
        Response response = doGet("translation");
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void downloadNotSupportedTranslation() {
        Response response = doGet("translation?lang=notSupported");
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    private GenericType<Map<String, String>> map() {
        return new GenericType<Map<String, String>>() {
        };
    }
}
