package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void everybody_can_see_backend_version() throws Exception {
        String version = doGet("version", String.class);
        assertNotNull(version);
    }

}