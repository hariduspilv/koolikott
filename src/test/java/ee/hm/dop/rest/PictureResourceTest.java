package ee.hm.dop.rest;

import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static javax.ws.rs.core.MediaType.WILDCARD_TYPE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpHeaders;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Picture;
import ee.hm.dop.utils.FileUtils;

public class PictureResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_PICTURE_URL = "picture/%s";

    @Test
    public void getPictureDataByName() {
        Response response = doGet(format(GET_PICTURE_URL, "picture1"), WILDCARD_TYPE);

        String cacheControl = (String) response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
        assertEquals("max-age=31536000", cacheControl);

        byte[] data = response.readEntity(byte[].class);
        char[] encodeHex = Hex.encodeHex(data);

        assertArrayEquals("656b6f6f6c696b6f7474".toCharArray(), encodeHex);
    }

    @Test
    public void getPictureDataByNameNoPicture() {
        Response response = doGet(format(GET_PICTURE_URL, "sakjndjsafnbjdsbfiudsfaiu"), WILDCARD_TYPE);

        assertEquals(HTTP_NOT_FOUND, response.getStatus());

        String cacheControl = (String) response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
        assertEquals("must-revalidate,no-cache,no-store", cacheControl);
    }

    @Test
    public void addPicture() throws IOException {
        login("39011220013");

        final FileDataBodyPart filePart = new FileDataBodyPart("picture", FileUtils.getFile("bookCover.jpg"));
        @SuppressWarnings("resource")
        FormDataMultiPart formDataMultiPart = (FormDataMultiPart) new FormDataMultiPart().bodyPart(filePart);

        Response response = doPut("picture", Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()));

        formDataMultiPart.close();

        assertEquals(200, response.getStatus());
        Picture picture = response.readEntity(Picture.class);
        assertNotNull(picture.getId());
        assertNotNull(picture.getName());
        assertNull(picture.getData());
    }
}
