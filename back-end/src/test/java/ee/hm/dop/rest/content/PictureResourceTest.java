package ee.hm.dop.rest.content;

import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.WILDCARD_TYPE;
import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Picture;
import ee.hm.dop.rest.PictureResource;
import ee.hm.dop.utils.ConfigurationProperties;
import ee.hm.dop.utils.DOPFileUtils;
import ezvcard.util.IOUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpHeaders;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class PictureResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_PICTURE_URL = "picture/%s";
    private static final String GET_SM_THUMBNAIL_URL = "picture/thumbnail/sm/%s";
    private static final String GET_SM_LARGE_THUMBNAIL_URL = "picture/thumbnail/sm_xs_xl/%s";
    private static final String GET_LG_THUMBNAIL_URL = "picture/thumbnail/lg/%s";
    private static final String GET_LG_LARGE_THUMBNAIL_URL = "picture/thumbnail/lg_xs/%s";
    private static final int SM_THUMBNAIL_WIDTH = 200;
    private static final int SM_THUMBNAIL_HEIGHT = 134;
    private static final int SM_LARGE_THUMBNAIL_WIDTH = 300;
    private static final int SM_LARGE_THUMBNAIL_HEIGHT = 200;
    private static final int LG_THUMBNAIL_WIDTH = (int) (300 * 1.1);
    private static final int LG_LARGE_THUMBNAIL_WIDTH = (int) (600  * 1.1);
    private static final String TEST_IMAGE_NAME = "src/test/resources/uploads/1/bookCover.jpg";
    private static final double DELTA = 1e-15;

    @Test
    public void getPictureDataByName() {
        Response response = doGet(format(GET_PICTURE_URL, "picture1"), WILDCARD_TYPE);

        String cacheControl = (String) response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
        Assert.assertEquals(PictureResource.MAX_AGE_1_YEAR, cacheControl);

        byte[] data = response.readEntity(byte[].class);
        char[] encodeHex = Hex.encodeHex(data);

        assertArrayEquals("656b6f6f6c696b6f7474".toCharArray(), encodeHex);
    }

    @Test
    public void getSMThumbnailDataByName() throws IOException {
        BufferedImage img = getThumbnail(GET_SM_THUMBNAIL_URL);
        if (img == null) {
            fail();
        }

        assertEquals(SM_THUMBNAIL_WIDTH, img.getWidth());
        assertEquals(SM_THUMBNAIL_HEIGHT, img.getHeight());
    }

    @Test
    public void getSMLargeThumbnailDataByName() throws IOException {
        BufferedImage img = getThumbnail(GET_SM_LARGE_THUMBNAIL_URL);
        if (img == null) {
            fail();
        }

        assertEquals(SM_LARGE_THUMBNAIL_WIDTH, img.getWidth());
        assertEquals(SM_LARGE_THUMBNAIL_HEIGHT, img.getHeight());
    }

    @Test
    public void getLGThumbnailDataByName() throws IOException {
        BufferedImage thumbnail = getThumbnail(GET_LG_THUMBNAIL_URL);
        if (thumbnail == null) {
            fail();
        }

        assertEquals(LG_THUMBNAIL_WIDTH, thumbnail.getWidth(), 1);
        compareAspectRatios(ImageIO.read(DOPFileUtils.getFile(TEST_IMAGE_NAME)), thumbnail);
    }

    @Test
    public void getLGLargeThumbnailDataByName() throws IOException {
        BufferedImage thumbnail = getThumbnail(GET_LG_LARGE_THUMBNAIL_URL);
        if (thumbnail == null) {
            fail();
        }

        assertEquals(LG_LARGE_THUMBNAIL_WIDTH, thumbnail.getWidth(), 1);
        compareAspectRatios(ImageIO.read(DOPFileUtils.getFile(TEST_IMAGE_NAME)), thumbnail);
    }

    @Test
    public void addPicture() throws IOException {
        login(USER_MAASIKAS_VAARIKAS);

        File f = DOPFileUtils.getFile(TEST_IMAGE_NAME);
        final StreamDataBodyPart filePart = new StreamDataBodyPart("picture", new ByteArrayInputStream(Base64.getEncoder().encode(Files.readAllBytes(f.toPath()))));

        @SuppressWarnings("resource")
        FormDataMultiPart formDataMultiPart = (FormDataMultiPart) new FormDataMultiPart().bodyPart(filePart);

        Response response = doPost("picture", Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()));
        formDataMultiPart.close();

        Picture picture = response.readEntity(Picture.class);
        assertNotNull(picture.getId());
        assertNotNull(picture.getName());
        assertNull(picture.getData());
    }

    @Test
    public void getPictureDataByNameNoPicture() {
        Response response = doGet(format(GET_PICTURE_URL, "sakjndjsafnbjdsbfiudsfaiu"), WILDCARD_TYPE);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        String cacheControl = (String) response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
        assertEquals("must-revalidate,no-cache,no-store", cacheControl);
    }

    @Test
    public void getMaxSize() {
        Long response = doGet("picture/maxSize", Long.class);
        assertEquals(Long.valueOf(23), response);
    }

    private void compareAspectRatios(BufferedImage image1, BufferedImage image2) {
        assertEquals(getAspectRatio(image1), getAspectRatio(image2), DELTA);
    }

    private double getAspectRatio(BufferedImage image) {
        if (image.getHeight() > image.getWidth()) {
            return image.getHeight() / image.getWidth();
        } else {
            return image.getWidth() / image.getHeight();
        }
    }

    private BufferedImage getThumbnail(String requestUrl) throws IOException {
        login(USER_MAASIKAS_VAARIKAS);

        String imgName = prepareTestImage();
        Response thumbnailResponse = doGet(format(requestUrl, imgName), WILDCARD_TYPE);

        String cacheControl = (String) thumbnailResponse.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
        assertEquals(PictureResource.MAX_AGE_1_YEAR, cacheControl);

        byte[] data = thumbnailResponse.readEntity(byte[].class);
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    private String prepareTestImage() throws IOException {
        File f = DOPFileUtils.getFile(TEST_IMAGE_NAME);
        String imgName = sha1Hex(Files.readAllBytes(f.toPath()));
        Response checkImgExistenceResponse = doGet(format(GET_PICTURE_URL, imgName), WILDCARD_TYPE);
        if (checkImgExistenceResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            addTestImage(f);
        }
        return imgName;
    }

    private void addTestImage(File file) throws IOException {
        FormDataMultiPart multiPart = multipart(file);
        Response response = doPost("picture", Entity.entity(multiPart, multiPart.getMediaType()));
        multiPart.close();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    private FormDataMultiPart multipart(File file) throws IOException {
        StreamDataBodyPart filePart = new StreamDataBodyPart("picture", new ByteArrayInputStream(Base64.getEncoder().encode(Files.readAllBytes(file.toPath()))));
        return (FormDataMultiPart) new FormDataMultiPart().bodyPart(filePart);
    }

    private void fail() {
        Assert.fail("Creating image from byte array failed");
    }
}
