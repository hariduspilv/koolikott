package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertNull;

public class SuggestResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_SUGGEST_URL = "suggest?q=%s";
    public static final String GET_SUGGEST_SYSTEM_TAG_URL = "suggest/tag?q=%s";

    @Test
    public void suggest_returns_nothing_for_tag_because_mock_is_unconfigured() throws Exception {
        List<String> response = doGet(format(GET_SUGGEST_URL, "test"), new GenericType<List<String>>(){});
        assertNull(response);
    }

    @Test
    public void suggest_returns_nothing_for_system_tag_because_mock_is_unconfigured() throws Exception {
        List<String> response = doGet(format(GET_SUGGEST_SYSTEM_TAG_URL, "test"), new GenericType<List<String>>(){});
        assertNull(response);
    }
}
