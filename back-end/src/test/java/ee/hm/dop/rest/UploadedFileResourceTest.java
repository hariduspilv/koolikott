package ee.hm.dop.rest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import org.junit.Test;

/**
 * Created by mart on 8.08.16.
 */
public class UploadedFileResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void getUploadedFile() throws IOException {
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
}
