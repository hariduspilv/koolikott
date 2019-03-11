package ee.hm.dop.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicUrlTests {

    @Autowired
    private TestRestTemplate restTemplate;

    String ehisSchoolUrl = "http://enda.ehis.ee/avaandmed/rest/oppeasutused/-/-/-/-/-/-/-/-/-/0/0/XML";

    @Test
    public void index_is_up(){
        is_up(ehisSchoolUrl,"Backend is up");
    }

    private void is_up(String url,String mesage) {
        ResponseEntity<String> health = restTemplate.getForEntity(url,String.class);
        assertEquals(HttpStatus.OK,health.getStatusCode());
    }
}
