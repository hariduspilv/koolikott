package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TranslationResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void downloadRussianTranslation() {
        Map<String, String> translations = doGet("translation?lang=rus", map());

        assertEquals(4, translations.size());
        assertEquals("FOO сообщение", translations.get("FOO"));
        assertEquals("Эстонский язык", translations.get("Estonian"));
        assertEquals("русский язык", translations.get("Russian"));
        assertEquals("Mатематика", translations.get("TOPIC_MATHEMATICS"));
    }

    @Test
    public void downloadEstonianTranslation() {
        Map<String, String> translations = doGet("translation?lang=est", map());

        assertEquals(4, translations.size());
        assertEquals("FOO sõnum", translations.get("FOO"));
        assertEquals("Eesti keeles", translations.get("Estonian"));
        assertEquals("Vene keel", translations.get("Russian"));
        assertEquals("Matemaatika", translations.get("TOPIC_MATHEMATICS"));
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
