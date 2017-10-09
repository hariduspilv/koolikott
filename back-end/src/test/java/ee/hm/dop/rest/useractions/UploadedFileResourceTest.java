package ee.hm.dop.rest.useractions;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.UploadedFile;
import org.apache.commons.configuration.Configuration;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static org.junit.Assert.*;

public class UploadedFileResourceTest extends ResourceIntegrationTestBase {

    @Inject
    private Configuration configuration;
    public static final String TEST_FILE_NAME = "testFileForPostUploadedFile";
    public static final String DOP_FILE_EXTENSION = ".dop";

    @Test
    public void getInvalidIdFile() {
        Response response = doGet("uploadedFile/" + NOT_EXISTS_ID, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void uploadFile() throws IOException {
        login(USER_SECOND);

        final File tempFile = File.createTempFile(TEST_FILE_NAME, DOP_FILE_EXTENSION);
        Response response = doPost("uploadedFile", Entity.entity(multipart(tempFile), MediaType.MULTIPART_FORM_DATA));

        UploadedFile uploadedFile = response.readEntity(UploadedFile.class);
        assertNotNull(uploadedFile.getId());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(expectedUrl(uploadedFile), uploadedFile.getUrl());
        assertEquals(tempFile.getName(), uploadedFile.getName());
        assertTrue("located in test folder", uploadedFile.getPath().startsWith("src/test/resources/uploads/2"));
    }

    @Test
    public void maxSize() {
        Response response = doGet("uploadedFile/maxSize");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(configuration.getString(DOCUMENT_MAX_FILE_SIZE)), response.readEntity(Long.class));
    }

    @Test
    public void everybody_can_ask_for_file_and_make_server_archive_it() throws Exception {
        Response response = doGet("uploadedFile/1/bookCover.jpg?archive=true", MediaType.APPLICATION_OCTET_STREAM_TYPE);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void everybody_can_ask_for_file() throws Exception {
        Response response = doGet("uploadedFile/1/bookCover.jpg", MediaType.APPLICATION_OCTET_STREAM_TYPE);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    private String expectedUrl(UploadedFile uploadedFile) {
        return configuration.getString(SERVER_ADDRESS) + "/rest/uploadedFile/" + uploadedFile.getId() + "/" + uploadedFile.getName();
    }

    private MultiPart multipart(File tempFile) {
        final FileDataBodyPart filePart = new FileDataBodyPart("file", tempFile);
        return new FormDataMultiPart().bodyPart(filePart);
    }
}
