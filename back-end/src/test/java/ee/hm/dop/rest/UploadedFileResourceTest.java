package ee.hm.dop.rest;

import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;
import static ee.hm.dop.utils.ConfigurationProperties.FILE_UPLOAD_DIRECTORY;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.UploadedFile;
import org.apache.commons.configuration.Configuration;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

/**
 * Created by mart on 8.08.16.
 */
public class UploadedFileResourceTest extends ResourceIntegrationTestBase {

    @Inject
    private Configuration configuration;

    @Test
    public void getFile() throws IOException {
        Response response = doGet("uploadedFile/1", MediaType.APPLICATION_OCTET_STREAM_TYPE);

        InputStream is = response.readEntity(InputStream.class);
        File tempFile = File.createTempFile("testFileForGetUploadedFile", ".dop");

        OutputStream outputStream = new FileOutputStream(tempFile);
        int read;
        byte[] bytes = new byte[10000];

        while ((read = is.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        outputStream.close();

        assertTrue(tempFile.exists());
        assertTrue(tempFile.isFile());
    }

    @Test
    public void getInvalidIdFile(){
        Response response = doGet("uploadedFile/1000", MediaType.APPLICATION_OCTET_STREAM_TYPE);
        assertEquals(HTTP_NOT_FOUND, response.getStatus());
    }

    @Test
    public void uploadFile() throws IOException {

        final String REGEX = "(\\d+)(?!.*\\d)";
        final String FILE_NAME = "testFileForPostUploadedFile";
        final String FILE_EXTENSION = ".dop";
        final File tempFile = File.createTempFile(FILE_NAME, FILE_EXTENSION);
        final FileDataBodyPart filePart = new FileDataBodyPart("file", tempFile);
        FormDataMultiPart formDataMultiPart = (FormDataMultiPart) new FormDataMultiPart().bodyPart(filePart);

        login("89012378912");

        Response response = doPost("uploadedFile", Entity.entity(formDataMultiPart,
                MediaType.MULTIPART_FORM_DATA), MediaType.APPLICATION_JSON_TYPE);

        UploadedFile uploadedFile = response.readEntity(UploadedFile.class);
        Long id = uploadedFile.getId();
        assertNotNull(id);
        String filename = uploadedFile.getPath().replaceAll(REGEX, "");
        assertEquals(HTTP_OK, response.getStatus());
        assertNotNull(uploadedFile.getId());
        assertEquals(configuration.getString(SERVER_ADDRESS) + "/rest/uploadedFile/" + id + "/" + uploadedFile.getName(), uploadedFile.getUrl());
        assertEquals(filename, uploadedFile.getPath().replaceAll(REGEX, ""));
    }

    @Test
    public void maxSize() {
        Response response = doGet("uploadedFile/maxSize");
        assertEquals(HTTP_OK, response.getStatus());
        assertEquals(Long.valueOf(configuration.getString(DOCUMENT_MAX_FILE_SIZE)), response.readEntity(Long.class));
        System.out.println("Test");
    }
}
