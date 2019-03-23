package ee.hm.dop.service.ehis;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertNotNull;

public class EhisInstitutionServiceTest extends ResourceIntegrationTestBase {

    private final String ehisSchoolUrl = "http://enda.ehis.ee/avaandmed/rest/oppeasutused/-/-/-/-/-/-/-/-/-/0/0/XML";

    @Test
    public void can_get_response_from_ehis_institutions() {
        Response response = doGet(ehisSchoolUrl);
        assertNotNull(response);
    }
}