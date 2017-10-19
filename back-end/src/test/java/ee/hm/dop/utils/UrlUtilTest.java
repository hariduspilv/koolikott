package ee.hm.dop.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class UrlUtilTest {

    private static final String SOURCE_MATERIAL = "https://www.youtube.com/watch?v=gSWbx3CvVUk";
    private static final String SOURCE_MATERIAL_WITHOUT_WWW = "https://youtube.com/watch?v=gSWbx3CvVUk";
    private static final String SOURCE_MATERIAL_WITHOUT_PROTOCOL = "www.youtube.com/watch?v=gSWbx3CvVUk";
    private static final String SOURCE_MATERIAL_WITHOUT_PROTOCOL_AND_WWW = "youtube.com/watch?v=gSWbx3CvVUk";
    private static final String SOURCE_MATERIAL_WITH_TRAILING_SLASH = "https://www.youtube.com/watch?v=gSWbx3CvVUk/";
    private static final String SOURCE_EMPTY = "";
    private static final String SOURCE_HOST = "www.youtube.com";
    private static final String DEFAULT_PROTOCOL = "http://";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getURLWithoutProtocolAndWWW_returns_url_without_protocol_and_www() throws Exception {
        String result = UrlUtil.getURLWithoutProtocolAndWWW(SOURCE_MATERIAL);
        assertEquals("Url", SOURCE_MATERIAL_WITHOUT_PROTOCOL_AND_WWW, result);

        String result2 = UrlUtil.getURLWithoutProtocolAndWWW(SOURCE_MATERIAL_WITHOUT_WWW);
        assertEquals("Url", SOURCE_MATERIAL_WITHOUT_PROTOCOL_AND_WWW, result2);
    }

    @Test
    public void getURLWithoutProtocolAndWWW_throws_exception_when_source_is_without_protocol() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Source has no protocol");
        UrlUtil.getURLWithoutProtocolAndWWW(SOURCE_MATERIAL_WITHOUT_PROTOCOL);
    }

    @Test
    public void getURLWithoutProtocolAndWWW_returns_nothing_when_source_is_empty() throws Exception {
        assertEquals("Url", null, UrlUtil.getURLWithoutProtocolAndWWW(SOURCE_EMPTY));
    }

    @Test
    public void getHost_returns_source_host() throws Exception {
        String result = UrlUtil.getHost(SOURCE_MATERIAL);
        assertEquals("Host", SOURCE_HOST, result);
    }

    @Test
    public void getHost_throws_exception_when_source_is_without_protocol() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Source has no protocol");
        UrlUtil.getHost(SOURCE_MATERIAL_WITHOUT_PROTOCOL);
    }

    @Test
    public void getHost_returns_nothing_when_source_is_empty() throws Exception {
        assertEquals("Host", null, UrlUtil.getHost(SOURCE_EMPTY));
    }

    @Test
    public void processURL_returns_url_without_trailing_slash() throws Exception {
        String result = UrlUtil.processURL(SOURCE_MATERIAL_WITH_TRAILING_SLASH);
        assertEquals(SOURCE_MATERIAL, result);
    }

    @Test
    public void processURL_returns_url_with_protocol_when_source_is_missing_it() throws Exception {
        String result = UrlUtil.processURL(SOURCE_MATERIAL_WITHOUT_PROTOCOL);
        assertEquals("Processed Url", DEFAULT_PROTOCOL + SOURCE_MATERIAL_WITHOUT_PROTOCOL, result);
    }

    @Test
    public void processURL_returns_nothing_when_source_is_empty() throws Exception {
        assertEquals("Processed Url", null, UrlUtil.processURL(SOURCE_EMPTY));
    }
}