package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.LicenseType;
import ee.hm.dop.model.Media;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MediaResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void everybody_can_access_media() throws Exception {
        Media media = doGet("media?id=1", Media.class);
        assertTrue(media != null);
    }

    @Test
    public void unauthorized_user_can_not_create_or_update_media() throws Exception {
        Media media = new Media();
        setMedia(media);

        Response response = doPost("media/create/", media);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        
        Response response2 = doPost("media/update/", media);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response2.getStatus());
    }

    @Test
    public void logged_in_user_can_create_media() throws Exception {
        login(USER_PEETER);

        Media media = new Media();
        setMedia(media);

        Media media1 = doPost("media/create/", media, Media.class);

        assertMedia(media1);
    }

    @Test
    public void updateMedia() throws Exception {
        login(USER_PEETER);

        Media media = doGet("media?id=1", Media.class);
        assertTrue(media != null);

        setMedia(media);

        Media media1 = doPost("media/update/", media, Media.class);

        assertMedia(media1);
    }

    private LicenseType licenceType1() {
        LicenseType licenseType = new LicenseType();
        licenseType.setName("CCBY");
        licenseType.setId(1L);
        return licenseType;
    }

    private void assertMedia(Media media1) {
        assertTrue(media1 != null);
        assertEquals("http://www.mysource.com", media1.getUrl());
        assertEquals("big author", media1.getAuthor());
        assertEquals("mysource", media1.getSource());
        assertEquals(1L,(long) media1.getLicenseType().getId());
        assertEquals("CCBY", media1.getLicenseType().getName());
        assertEquals("Title", media1.getTitle());
    }

    private void setMedia(Media media) {
        media.setUrl("www.mysource.com");
        media.setAuthor("big author");
        media.setSource("mysource");
        media.setLicenseType(licenceType1());
        media.setTitle("Title");
    }
}