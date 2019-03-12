package ee.hm.dop.rest;

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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicUrlTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EhisInstitutionService ehisInstitutionService;

    private final String ehisSchoolUrl = "http://enda.ehis.ee/avaandmed/rest/oppeasutused/-/-/-/-/-/-/-/-/-/0/0/XML";

    @Test
    public void can_get_response_from_ehis_institutions(){
        get_response(ehisSchoolUrl);
    }

    @Test
    public void get_ehis_inst_info() throws MalformedURLException, DocumentException {
        ehisInstitutionService.getInstitutionsAndUpdateDb();
    }

    private void get_response(String url) {
        ResponseEntity<String> health = restTemplate.getForEntity(url,String.class);
        assertEquals(HttpStatus.OK,health.getStatusCode());
    }
}
