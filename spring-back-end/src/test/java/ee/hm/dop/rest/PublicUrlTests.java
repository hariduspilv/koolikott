package ee.hm.dop.rest;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.service.ehis.EhisInstitutionService;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;

import static ee.hm.dop.utils.ConfigurationProperties.XROAD_EHIS_INSTITUTIONS_LIST;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicUrlTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EhisInstitutionService ehisInstitutionService;

    @Autowired
    private Configuration configuration;

    @Test
    public void can_get_response_from_ehis_institutions(){
        String ehisSchoolUrl = configuration.getString(XROAD_EHIS_INSTITUTIONS_LIST);
        ResponseEntity<String> health = restTemplate.getForEntity(ehisSchoolUrl,String.class);
        assertEquals(HttpStatus.OK,health.getStatusCode());
    }

    @Test
    public void make_real_ehis_institution_update() throws MalformedURLException, DocumentException {
        ehisInstitutionService.getInstitutionsAndUpdateDb();
    }
}
